package net.jsmith.java.decomp.container;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TypeContainerUtils {

	public static CompletableFuture< TypeContainer > createTypeContainerFromJar( Path jarPath ) {
		return CompletableFuture.supplyAsync( ( ) -> {
			try {
				URI jarURI = new URI( "jar:" + jarPath.toAbsolutePath( ).toUri( ).toString( ) );
				Map< String, String > env = new HashMap< >( );
	            env.put( "create", "false" );
	            
	            FileSystem jarFileSystem = FileSystems.newFileSystem( jarURI, env, null );
	            TypeContainer container = new NIO2TypeContainer( jarPath.getFileName( ).toString( ), jarFileSystem );
				return container;
			}
			catch( IOException | URISyntaxException err ) {
				throw new RuntimeException( err );
			}
		} );
	}
	
}
