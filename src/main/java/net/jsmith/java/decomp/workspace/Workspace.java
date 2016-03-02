package net.jsmith.java.decomp.workspace;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.jsmith.java.decomp.listener.BroadcastListener;

public interface Workspace {

	String getName( );
	
	List< Container > getContainers( );
	
	void openContainerAtPath( Path containerPath );
	
	void close( );
	
	CompletableFuture< List< Type > > resolveType( String typeName );
	
	BroadcastListener< Container > onContainerOpened( );
	BroadcastListener< Container > onContainerClosed( );
	BroadcastListener< Throwable > onError( );
	
}
