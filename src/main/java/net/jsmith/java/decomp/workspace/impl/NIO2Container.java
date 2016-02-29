package net.jsmith.java.decomp.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.decomp.utils.IOUtils;

public class NIO2Container extends AbstractContainer {

	private static final Logger LOG = LoggerFactory.getLogger( NIO2Container.class );
	
	private final FileSystem fileSystem;
	private final Path rootPath;
	
	public NIO2Container( String name, WorkspaceImpl workspace, FileSystem fileSystem ) {
		this( name, workspace, fileSystem, fileSystem.getPath( "/" ) );
	}
	
	public NIO2Container( String name, WorkspaceImpl workspace, FileSystem fileSystem, Path rootPath ) {
		super( name, workspace );
		
		this.fileSystem = Objects.requireNonNull( fileSystem, "fileSystem" );
		this.rootPath = Objects.requireNonNull( rootPath, "rootPath" );
		
		this.withReferenceAsync( this::loadAllTypes );
	}

	@Override
	protected InputStream getInputStream( String typeName ) throws IOException {
		Path path = this.rootPath.resolve( typeName.replace( '.', '/' ) + ".class" );
		if( !Files.isRegularFile( path ) ) {
			return null;
		}
		return Files.newInputStream( path );
	}
	
	@Override
	protected void implClose( ) {
		IOUtils.safeClose( this.fileSystem );
		
		super.implClose( );
	}

	private void loadAllTypes( ) {
		Stream< Path > stream = null;
		try {
			stream = Files.walk( this.rootPath );
			
			stream.filter( ( path ) -> {
				return Files.isRegularFile( path ) && path.getFileName( ).toString( ).endsWith( ".class" );
			} ).map( ( path ) -> {
				String typeName = this.rootPath.relativize( path ).toString( );
				typeName = typeName.substring( 0, typeName.lastIndexOf( '.' ) );
				typeName = typeName.replace( '/', '.' ).replace( '\\', '.' );
				return typeName;
			} ).sorted( ).forEach( this::loadType );
		}
		catch( IOException ioe ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading all types for container '{}'.", this.getName( ), ioe );
			}
			this.getWorkspace( ).onError( ioe );
		}
		finally {
			IOUtils.safeClose( stream );
		}
	}
	
}
