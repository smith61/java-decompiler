package net.jsmith.java.byteforge.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.jsmith.java.byteforge.workspace.events.TypeLoadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.byteforge.utils.IOUtils;
import net.jsmith.java.byteforge.workspace.Container;
import net.jsmith.java.byteforge.workspace.Type;

public abstract class AbstractContainer extends Referenceable implements Container {

	private static final Logger LOG = LoggerFactory.getLogger( AbstractContainer.class );
	
	private final String name;
	private final WorkspaceImpl workspace;
	
	private final List< Type > loadedTypes = new ArrayList< >( );
	
	protected AbstractContainer( String name, WorkspaceImpl workspace ) {
		this.name = Objects.requireNonNull( name, "name" );
		this.workspace = Objects.requireNonNull( workspace, "workspace" );
		
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
		}
		this.getWorkspace( ).getEventBus( ).post( new TypeLoadEvent( result ) );
	}
	
	protected void implClose( ) {
		this.workspace.removeContainer( this );
		this.workspace.decReference( );
		
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Container with name '{}' closed.", this.getName( ) );
		}
	}
	
	protected abstract InputStream getInputStream( String typeName ) throws IOException;
	
}
