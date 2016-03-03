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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.decomp.listener.BroadcastListener;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Reference;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.Workspace;

public class WorkspaceImpl extends Referenceable implements Workspace {
	
	private final Logger LOG = LoggerFactory.getLogger( WorkspaceImpl.class );
	
	private final String name;
	private final List< AbstractContainer > containers;
	
	private final BroadcastListener< Container > onContainerOpened;
	private final BroadcastListener< Container > onContainerClosed;
	private final BroadcastListener< Throwable > onError;
	
	public WorkspaceImpl( String name ) {
		this.name = Objects.requireNonNull( name, "name" );
		
		this.containers = new ArrayList< >( );
		
		this.onContainerOpened = new BroadcastListener< >( );
		this.onContainerClosed = new BroadcastListener< >( );
		this.onError = new BroadcastListener< >( );
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
		this.withReferenceAsync( ( ) -> {
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
				this.onContainerOpened.on( container );
			}
			catch( Throwable err ) {
				if( LOG.isErrorEnabled( ) ) {
					LOG.error( "Error opening container at path '{}' into workspace '{}'.", path, this.getName( ), err );
				}
				this.onError( err );
			}
		} );
	}
	
	@Override
	public void close( ) {
		super.close( );
		List< AbstractContainer > clone;
		synchronized( this.containers ) {
			clone = new ArrayList< >( this.containers );
		}
		for( Container container : clone ) {
			container.close( );
		}
	}
	
	@Override
	public CompletableFuture< List< Type > > resolveReference( Reference reference ) {
		return this.withReferenceAsync( ( ) -> {
			return new ReferenceResolver( this, reference ).resolve( );
		} );
	}
	
	@Override
	public BroadcastListener< Container > onContainerOpened( ) {
		return this.onContainerOpened;
	}

	@Override
	public BroadcastListener< Container > onContainerClosed( ) {
		return this.onContainerClosed;
	}

	@Override
	public BroadcastListener< Throwable > onError( ) {
		return this.onError;
	}

	protected final void onError( Throwable t ) {
		this.onError.on( t );
	}
	
	protected final void removeContainer( AbstractContainer container ) {
		synchronized( this.containers ) {
			this.containers.remove( container );
		}
		this.onContainerClosed.on( container );
	}

	protected void implClose( ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Workspace with name '{}' closed.", this.getName( ) );
		}
	}
	
}
