package net.jsmith.java.decomp.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.decomp.listener.BroadcastListener;
import net.jsmith.java.decomp.utils.IOUtils;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Type;

public abstract class AbstractContainer extends Referenceable implements Container {

	private static final Logger LOG = LoggerFactory.getLogger( AbstractContainer.class );
	
	private final String name;
	private final WorkspaceImpl workspace;
	
	private final List< Type > loadedTypes = new ArrayList< >( );
	
	private final BroadcastListener< Type > onTypeLoaded;
	
	protected AbstractContainer( String name, WorkspaceImpl workspace ) {
		this.name = Objects.requireNonNull( name, "name" );
		this.workspace = Objects.requireNonNull( workspace, "workspace" );
		
		this.onTypeLoaded = new BroadcastListener< >( );
		
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
	public BroadcastListener< Type > onTypeLoaded( ) {
		return this.onTypeLoaded;
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
		this.onTypeLoaded.on( result );
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
