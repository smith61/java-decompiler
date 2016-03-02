package net.jsmith.java.decomp.decompiler;

import java.util.concurrent.CompletableFuture;

import net.jsmith.java.decomp.document.Document;
import net.jsmith.java.decomp.workspace.Type;

public interface Decompiler {

	String getName( );
	
	CompletableFuture< String > decompile( Type type );
	
	CompletableFuture< Document > decompileRichText( Type type );
	
}
