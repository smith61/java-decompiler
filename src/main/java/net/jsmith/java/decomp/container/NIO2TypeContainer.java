package net.jsmith.java.decomp.container;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import net.jsmith.java.decomp.gui.ErrorDialog;

public class NIO2TypeContainer extends AbstractTypeContainer {

	private final FileSystem fileSystem;
	private final Path rootPath;
	
	public NIO2TypeContainer( String name, FileSystem fileSystem ) {
		this( name, fileSystem, fileSystem.getPath( "/" ) );
	}
	
	public NIO2TypeContainer( String name, FileSystem fileSystem, Path rootPath ) {
		super( name );
		
		this.fileSystem = fileSystem;
		this.rootPath = rootPath;
		
		CompletableFuture.runAsync( ( ) -> {
			try {
				loadAllContainedTypes( );
			}
			catch( IOException ioe ) {
				ErrorDialog.displayError( "Error loading contained types", "Error loading contained types for: " + name, ioe );
			}
		} );
	}
	
	@Override
	public void close( ) throws IOException {
		this.fileSystem.close( );
	}

	@Override
	protected InputStream getStreamForType( String internalName ) throws IOException {
		Path loc = this.rootPath.resolve( internalName.replace( '.', '/' ) + ".class" );
		if( !Files.isRegularFile( loc ) ) {
			return null;
		}
		
		return Files.newInputStream( loc );
	}
	
	private void loadAllContainedTypes( ) throws IOException {
		try( Stream< Path > stream = Files.walk( this.rootPath ) ) {
			stream.filter( ( path ) -> {
				return Files.isRegularFile( path ) && path.getFileName( ).toString( ).endsWith( ".class");
			} ).map( ( path ) -> {
				String typeName = this.rootPath.relativize( path ).toString( );
				typeName = typeName.substring( 0, typeName.length( ) - ".class".length( ) );
				typeName = typeName.replace( '/', '.' ).replace( '\\', '.' );
				return typeName;
			} ).sorted( ( l, r ) -> {
				return l.compareTo( r );
			} ).forEach( this::loadType );
		}
	}
	
}
