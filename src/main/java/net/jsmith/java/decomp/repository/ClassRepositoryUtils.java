package net.jsmith.java.decomp.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ClassRepositoryUtils {

	public static ClassRepository loadFromJar( Path jarPath ) throws IOException {
		try {
			URI jarURI = new URI( "jar:" + jarPath.toUri( ).toString( ) );
			Map< String, String > env = new HashMap< >( );
			
			FileSystem zipSystem = FileSystems.newFileSystem( jarURI, env, null );
			return new NIOPathClassRepository( jarPath.getFileName( ).toString( ), zipSystem.getPath( "/" ) );
		}
		catch( URISyntaxException use ) {
			throw new IOException( use );
		}
	}
	
}
