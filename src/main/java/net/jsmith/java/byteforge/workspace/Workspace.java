package net.jsmith.java.byteforge.workspace;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.eventbus.EventBus;

public interface Workspace {

	String getName( );
	
	List< Container > getContainers( );
	
	void openContainerAtPath( Path containerPath );
	
	void close( );
	
	CompletableFuture< List< Type > > resolveReference( Reference reference );
	
	EventBus getEventBus( );
}
