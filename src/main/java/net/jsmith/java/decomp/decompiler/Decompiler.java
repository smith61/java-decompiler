package net.jsmith.java.decomp.decompiler;

import net.jsmith.java.decomp.document.Document;
import net.jsmith.java.decomp.workspace.Type;

public interface Decompiler {

	String getName( );
	
	String decompile( Type type );
	
	Document decompileRichText( Type type );
	
}
