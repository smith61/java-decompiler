package net.jsmith.java.byteforge.workspace;

import java.util.List;

import net.jsmith.java.byteforge.listener.BroadcastListener;

public interface Container {

	String getName( );
	
	Workspace getWorkspace( );
	
	Type findType( String typeName );
	
	List< Type > getContainedTypes( );
	
	void close( );
	
	BroadcastListener< Type > onTypeLoaded( );
	
}
