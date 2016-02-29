package net.jsmith.java.decomp.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.decomp.utils.IOUtils;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Type;

public abstract class AbstractContainer extends Referenceable implements Container {

	private static final Logger LOG = LoggerFactory.getLogger( AbstractContainer.class );
	
	private final String name;
	private final WorkspaceImpl workspace;
	
	private final AtomicReference< Consumer< ? super Type > > typeLoadedListener;
	private final List< Type > loadedTypes = new ArrayList< >( );
	
	protected AbstractContainer( String name, WorkspaceImpl workspace ) {
		this.name = Objects.requireNonNull( name, "name" );
		this.workspace = Objects.requireNonNull( workspace, "workspace" );
		
		this.typeLoadedListener = new AtomicReference< >( null );
		
		this.workspace.incReference( );
	}
	
	@Override
	public final String getName( ) {
		return this.name;
	}
	
	@Override
	public final WorkspaceImpl getWorkspace( ) {
		return this.workspace;
	}
	
	@Override
	public final Type findType( String typeName ) {
		return this.withReference( ( ) -> {
			synchronized( this.loadedTypes ) {
				for( Type type : this.loadedTypes ) {
					if( type.getMetadata( ).getFullName( ).equals( typeName ) ) {
						return type;
					}
				}
				return null;
			}
		} );
	}
	
	@Override
	public final List< Type > getContainedTypes( ) {
		return this.withReference( ( ) -> {
			synchronized( this.loadedTypes ) {
				return new ArrayList< >( this.loadedTypes );
			}
		} );
	}
	
	@Override
	public final void setOnTypeLoadedListener( Consumer< ? super Type > l ) {
		this.withReference( ( ) -> {
			synchronized( this.loadedTypes ) {
				// Ensure proper ordering of calls to the new listener
				//  All previously loaded types should be provided
				//  before sending any new types.
				this.typeLoadedListener.set( l );
				if( l != null ) {
					for( Type loadedType : this.loadedTypes ) {
						l.accept( loadedType );
					}
				}
			}
			return null;
		} );
	}
	
	@Override
	protected final void scheduleClose( ) {
		this.workspace.schedule( this::implClose );
	}
	
	protected final void loadType( String typeName ) {
		if( LOG.isDebugEnabled( ) ) {
			LOG.debug( "Loading type with name '{}' into container '{}'.", typeName, this.getName( ) );
		}
		Type result = null;
		
		InputStream is = null;
		try {
			is = this.getInputStream( typeName );
			
			result = new TypeImpl( this, MetadataLoader.loadFromStream( is ) );
		}
		catch( IOException ioe ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading type '{}' into container '{}'.", typeName, this.getName( ), ioe );
			}
			this.workspace.onError( ioe );
		}
		finally {
			IOUtils.safeClose( is );
		}
		
		synchronized( this.loadedTypes ) {
			this.loadedTypes.add( result );
			Consumer< ? super Type > l = this.typeLoadedListener.get( );
			if( l != null ) {
				l.accept( result );
			}
		}
	}
	
	protected void implClose( ) {
		this.workspace.removeContainer( this );
		this.workspace.decReference( );
	}
	
	protected abstract InputStream getInputStream( String typeName ) throws IOException;
	
}
