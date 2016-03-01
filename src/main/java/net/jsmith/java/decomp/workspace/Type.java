package net.jsmith.java.decomp.workspace;

import java.io.IOException;
import java.io.InputStream;

public interface Type {

	Container getContainer( );

	Metadata getMetadata( );
	
	InputStream getInputStream( ) throws IOException;
	
}
