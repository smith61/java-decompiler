package net.jsmith.java.decomp.reference;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TypeContainerUtils {

    public static TypeContainer createFromJar( Path jarPath ) throws IOException {
        try {
            URI jarURI = new URI( "jar:" + jarPath.toUri( ).toString( ) );
            Map< String, String > env = new HashMap< >( );
            
            FileSystem zipSystem = FileSystems.newFileSystem( jarURI, env, null );
            return new NIOPathTypeContainer( jarPath.getFileName( ).toString( ), zipSystem, zipSystem.getPath( "/" ) );
        }
        catch( Throwable err ) {
            throw new IOException( err );
        }
    }
    
}
