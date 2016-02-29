package net.jsmith.java.decomp.workspace.impl;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.Workspace;

public class WorkspaceImpl extends Referenceable implements Workspace {
	
	private final Logger LOG = LoggerFactory.getLogger( WorkspaceImpl.class );
	
	private final ExecutorService threadPool;
	
	private final String name;
	private final List< AbstractContainer > containers;
	
	private final AtomicReference< Consumer< ? super Container > > containerOpenedListener;
	private final AtomicReference< Consumer< ? super Container > > containerClosedListener;
	private final AtomicReference< Consumer< ? super Throwable > > errorListener;
	
	public WorkspaceImpl( String name ) {
		this.name = Objects.requireNonNull( name, "name" );
		
		this.threadPool = Executors.newCachedThreadPool( );
		
		this.containers = new ArrayList< >( );
		
		this.containerOpenedListener = new AtomicReference< >( );
		this.containerClosedListener = new AtomicReference< >( );
		this.errorListener = new AtomicReference< >( );
	}
	
	@Override
	public String getName( ) {
		return this.name;
	}

	@Override
	public List< Container > getContainers( ) {
		return this.withReference( ( ) -> {
			synchronized( this.containers ) {
				return new ArrayList< >( this.containers );
			}
		} );
	}
	
	@Override
	public void openContainerAtPath( Path path ) {
		this.incReference( );
		this.schedule( ( ) -> {
			if( LOG.isDebugEnabled( ) ) {
				LOG.debug( "Opening container at path '{}' into workspace '{}'.", path, this.getName( ) );
			}
			try {
				URI uri = new URI( "jar:" + path.toUri( ).toString( ) );
				Map< String, String > env = new HashMap< >( );
				
				FileSystem fileSystem = FileSystems.newFileSystem( uri, env, null );
				AbstractContainer container = new NIO2Container( path.getFileName( ).toString( ), this, fileSystem );
				synchronized( this.containers ) {
					this.containers.add( container );
				}
				Consumer< ? super Container > l = this.containerOpenedListener.get( );
				if( l != null ) {
					l.accept( container );
				}
			}
			catch( Throwable err ) {
				if( LOG.isErrorEnabled( ) ) {
					LOG.error( "Error opening container at path '{}' into workspace '{}'.", path, this.getName( ), err );
				}
				this.onError( err );
			}
			finally {
				this.decReference( );
			}
		} );
	}
	
	@Override
	public void close( ) {
		super.close( );
		synchronized( this.containers ) {
			for( Container container : this.containers ) {
				container.close( );
			}
		}
	}
	
	@Override
	public CompletableFuture< List< Type > > resolveType( String typeName ) {
		return this.withReference( ( ) -> {
			return CompletableFuture.supplyAsync( ( ) -> {
				return this.withReference( ( ) -> {
					List< Type > types = new ArrayList< >( );
					synchronized( this.containers ) {
						for( Container container : this.containers ) {
							Type type = container.findType( typeName );
							if( type != null ) {
								types.add( type );
							}
						}
					}
					return types;
				} );
			}, this.threadPool );
		} );
	}

	@Override
	public void setContainerOpenedListener( Consumer< ? super Container > l ) {
		this.withReference( ( ) -> {
			this.containerOpenedListener.set( l );
			if( l != null ) {
				synchronized( this.containers ) {
					for( Container container : this.containers ) {
						l.accept( container );
					}
				}
			}
			return null;
		} );
	}

	@Override
	public void setContainerClosedListener( Consumer< ? super Container > l ) {
		this.withReference( ( ) -> {
			this.containerClosedListener.set( l );
			return null;
		} );
	}

	@Override
	public void setErrorListener( Consumer< ? super Throwable > l ) {
		this.withReference( ( ) -> {
			this.errorListener.set( l );
			return null;
		} );
	}
	
	@Override
	protected final void scheduleClose( ) {
		this.schedule( this::implClose );
	}
	
	protected final void onError( Throwable t ) {
		Consumer< ? super Throwable > listener = this.errorListener.get( );
		if( listener != null ) {
			listener.accept( t );
		}
	}
	
	protected final void schedule( Runnable runnable ) {
		this.threadPool.submit( runnable );
	}
	
	protected final void removeContainer( AbstractContainer container ) {
		synchronized( this.containers ) {
			this.containers.remove( container );
		}
		Consumer< ? super Container > listener = this.containerClosedListener.get( );
		if( listener != null ) {
			listener.accept( container );
		}
	}

	protected void implClose( ) {
		this.threadPool.shutdown( );
	}
	
}
