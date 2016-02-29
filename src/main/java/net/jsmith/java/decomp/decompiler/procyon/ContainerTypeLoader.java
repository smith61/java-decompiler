package net.jsmith.java.decomp.decompiler.procyon;

import java.io.InputStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;

import net.jsmith.java.decomp.utils.IOUtils;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Type;

public class ContainerTypeLoader implements ITypeLoader {

	private static final Logger LOG = LoggerFactory.getLogger( ContainerTypeLoader.class );
	
	private final Container container;
	
	private final MetadataSystem metadataSystem;
	
	public ContainerTypeLoader( Container container ) {
		this.container = Objects.requireNonNull( container, "container" );
		
		this.metadataSystem = new MetadataSystem( this );
		this.metadataSystem.setEagerMethodLoadingEnabled( true );
	}
	
	@Override
	public boolean tryLoadType( String internalName, Buffer buffer ) {
		Type type = this.container.findType( internalName );
		if( type == null ) {
			return false;
		}
		
		InputStream is = null;
		try {
			is = type.getInputStream( ).get( );
			if( is == null ) {
				return false;
			}
			
			byte[ ] bytes = IOUtils.readFully( is );
			
			buffer.reset( bytes.length );
			System.arraycopy( bytes, 0, buffer.array( ), 0, bytes.length );
			return true;
		}
		catch( Exception err ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading type with name '{}' from container '{}'.", internalName, this.container.getName( ), err );
			}
			return false;
		}
		finally {
			IOUtils.safeClose( is );
		}
	}
	
	public MetadataSystem getMetadataSystem( ) {
		return this.metadataSystem;
	}

}
