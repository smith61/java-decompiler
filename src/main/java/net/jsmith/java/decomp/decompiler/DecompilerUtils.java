package net.jsmith.java.decomp.decompiler;

import java.util.concurrent.CompletableFuture;

import net.jsmith.java.decomp.decompiler.procyon.ProcyonDecompiler;
import net.jsmith.java.decomp.utils.ThreadPools;
import net.jsmith.java.decomp.workspace.Type;

public class DecompilerUtils {
	
	public static final Decompiler PROCYON = new ProcyonDecompiler( );

	public static CompletableFuture< String > defaultDecompile( Type type ) {
		return ThreadPools.supplyBackground( ( ) -> {
			return PROCYON.decompile( type );
		} );
	}
	
}
