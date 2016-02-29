package net.jsmith.java.decomp.workspace;

import java.util.List;
import java.util.function.Consumer;

public interface Container {

	String getName( );
	
	Workspace getWorkspace( );
	
	Type findType( String typeName );
	
	List< Type > getContainedTypes( );
	
	void close( );
	
	void setOnTypeLoadedListener( Consumer< ? super Type > l );
	
}
