package net.jsmith.java.decomp.workspace;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface Type {

	Container getContainer( );

	Metadata getMetadata( );
	
	CompletableFuture< InputStream > getInputStream( );
	
}
