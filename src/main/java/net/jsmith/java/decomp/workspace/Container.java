package net.jsmith.java.decomp.workspace;

import java.util.List;

import net.jsmith.java.decomp.listener.BroadcastListener;

public interface Container {

	String getName( );
	
	Workspace getWorkspace( );
	
	Type findType( String typeName );
	
	List< Type > getContainedTypes( );
	
	void close( );
	
	BroadcastListener< Type > onTypeLoaded( );
	
}
