package net.jsmith.java.byteforge.decompiler;

import net.jsmith.java.byteforge.workspace.Type;

public interface Decompiler {

	String getName( );
	
	String decompile( Type type );
	
}
