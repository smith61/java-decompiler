package net.jsmith.java.byteforge.decompiler;

import java.util.concurrent.CompletableFuture;

import net.jsmith.java.byteforge.decompiler.procyon.ProcyonDecompiler;
import net.jsmith.java.byteforge.utils.ThreadPools;
import net.jsmith.java.byteforge.workspace.Type;

public class DecompilerUtils {
	
	public static final Decompiler PROCYON = new ProcyonDecompiler( );

	public static CompletableFuture< String > defaultDecompile( Type type ) {
		return ThreadPools.supplyBackground( ( ) -> {
			return PROCYON.decompile( type );
		} );
	}
	
}
