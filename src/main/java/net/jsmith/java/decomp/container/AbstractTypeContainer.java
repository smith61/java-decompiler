package net.jsmith.java.decomp.container;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.jsmith.java.decomp.asm.TypeMetadataLoader;
import net.jsmith.java.decomp.gui.PlatformExecutor;

public abstract class AbstractTypeContainer implements TypeContainer, ITypeLoader {

	private static final Logger LOG = LoggerFactory.getLogger( AbstractTypeContainer.class );
	
	private final String name;
	
	private final MetadataSystem metadataSystem;
	
	private final ObservableMap< String, Type > containedTypes;
	private final ObservableMap< String, Type > containedTypesView;
	
	private final Object LOCK = new Object( );
	private List< TypeMetadata > pendingUpdates;
	
	protected AbstractTypeContainer( String name ) {
		this.name = name;
		
		this.metadataSystem = new MetadataSystem( this );
		this.metadataSystem.setEagerMethodLoadingEnabled( false );
		
		this.containedTypes = FXCollections.observableHashMap( );
		this.containedTypesView = FXCollections.unmodifiableObservableMap( this.containedTypes );
		
		this.pendingUpdates = new ArrayList< >( );
	}
	
	protected final MetadataSystem getMetadataSystem( ) {
		return this.metadataSystem;
	}
	
	protected final void loadType( String typeName ) {
		try {
			if( LOG.isInfoEnabled( ) ) {
				LOG.info( "Loading type '{}' into container '{}'.", typeName, this.getName( ) );
			}
			TypeMetadata metadata;
			try( InputStream is = this.getStreamForType( typeName ) ) {
				if( is == null ) {
					if( LOG.isWarnEnabled( ) ) {
						LOG.warn( "Container '{}' attempted to load type '{}' it could not resolve.", this.getName( ), typeName );
					}
					return;
				}
				metadata = TypeMetadataLoader.loadMetadataFromStream( is );
			}
			
			boolean scheduleUpdate;
			synchronized( LOCK ) {
				scheduleUpdate = this.pendingUpdates.isEmpty( );
				this.pendingUpdates.add( metadata );
			}
			if( scheduleUpdate ) {
				if( LOG.isDebugEnabled( ) ) {
					LOG.debug( "Scheduling update for container '{}'.", this.getName( ) );
				}
				PlatformExecutor.INSTANCE.execute( ( ) -> {
					List< TypeMetadata > updates;
					synchronized( LOCK ) {
						updates = this.pendingUpdates;
						this.pendingUpdates = new ArrayList< >( );
					}
					if( LOG.isDebugEnabled( ) ) {
						LOG.debug( "Publishing '{}' updates for container '{}'.", updates.size( ), this.getName( ) );
					}
					for( TypeMetadata update : updates ) {
						if( LOG.isTraceEnabled( ) ) {
							LOG.trace( "Publishing update for type '{}' in container '{}'.", update.getFullName( ), this.getName( ) );
						}
						Type type = new Type( this, update );
						this.containedTypes.put( update.getFullName( ), type );
					}
					if( LOG.isDebugEnabled( ) ) {
						LOG.debug( "Published '{}' updates for container '{}'.", updates.size( ), this.getName( ) );
					}
				} );
			}
		}
		catch( Throwable err ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading type '{}' into container '{}'.", typeName, this.getName( ), err );
			}
		}
	}
	
	@Override
	public final String getName( ) {
		return this.name;
	}
	
	@Override
	public final List< Type > resolveType( String typeName ) {
		if( this.containedTypes.containsKey( typeName ) ) {
			return Arrays.asList( this.containedTypes.get( typeName ) );
		}
		return Collections.emptyList( );
	}
	
	@Override
	public final ObservableMap< String, Type > getContainedTypes( ) {
		return this.containedTypesView;
	}
	
	public final boolean tryLoadType( String internalName, Buffer buffer ) {
		InputStream is = null;
		try {
			is = this.getStreamForType( internalName );
			if( is == null ) {
				return false;
			}
			if( LOG.isDebugEnabled( ) ) {
				LOG.debug( "Loading data for type '{}' from container '{}'.", internalName, this.getName( ) );
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream( );
			
			int read;
			byte[ ] buf = new byte[ 4096 ];
			while( ( read = is.read( buf ) ) >= 0 ) {
				baos.write( buf, 0, read );
			}
			
			buf = baos.toByteArray( );
			buffer.reset( buf.length );
			System.arraycopy( buf, 0, buffer.array( ), 0, buf.length );
			return true;
		}
		catch( IOException ioe ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading data from stream for type '{}' in container '{}'.", internalName, this.getName( ), ioe );
			}
			return false;
		}
		finally {
			if( is != null ) {
				try {
					is.close( );
				}
				catch( IOException ioe ) { }
			}
		}
	}
	
	protected abstract InputStream getStreamForType( String internalName ) throws IOException;
	
}
