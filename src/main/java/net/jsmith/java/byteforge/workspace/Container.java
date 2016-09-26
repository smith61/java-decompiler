package net.jsmith.java.byteforge.workspace;

import java.util.List;

public interface Container {

	String getName( );
	
	Workspace getWorkspace( );
	
	Type findType( String typeName );
	
	List< Type > getContainedTypes( );
	
	void close( );
	
}
