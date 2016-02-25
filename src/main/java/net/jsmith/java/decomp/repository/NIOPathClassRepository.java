package net.jsmith.java.decomp.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.strobel.assembler.metadata.Buffer;

public class NIOPathClassRepository implements ClassRepository {

	private final String name;
	private final Path rootPath;
	
	private final Set< String > resolvableTypes;
	
	public NIOPathClassRepository( String name, Path rootPath ) throws IOException {
		this.name = name;
		this.rootPath = rootPath;
		
		this.resolvableTypes = NIOPathClassRepository.findResolvableTypes( rootPath );
	}
	
	@Override
	public boolean tryLoadType( String internalName, Buffer buffer ) {
		try {
			Path classPath = this.rootPath.resolve( internalName.replace( '.', '/' ) + ".class" );
			if( !Files.exists( classPath ) ) {
				return false;
			}
			
			byte[ ] bytes = Files.readAllBytes( classPath );
			buffer.reset( bytes.length );
			System.arraycopy( bytes, 0, buffer.array( ), 0, bytes.length );
			return true;
		}
		catch( IOException ioe ) {
			ioe.printStackTrace( );
			return false;
		}
	}

	@Override
	public Set< String > getResolvableTypes( ) {
		return Collections.unmodifiableSet( this.resolvableTypes );
	}

	@Override
	public String getName( ) {
		return this.name;
	}
	
	private static Set< String > findResolvableTypes( Path rootPath ) throws IOException {
		try( Stream< Path > stream = Files.walk( rootPath ) ) {
			return stream.filter( ( Path path ) -> {
				return path.getFileName( ) != null && path.getFileName( ).toString( ).endsWith( ".class" );
			} ).map( ( Path path ) -> {
				String typeName = rootPath.relativize( path ).toString( );
				typeName = typeName.substring( 0, typeName.length( ) - ".class".length( ) );
				typeName = typeName.replace( '/', '.' ).replace( '\\', '.' );
				return typeName;
			} ).collect( Collectors.toSet( ) );
		}
	}
	
}
