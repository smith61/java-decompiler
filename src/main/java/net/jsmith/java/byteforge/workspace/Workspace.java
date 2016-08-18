package net.jsmith.java.byteforge.workspace;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.eventbus.EventBus;
import net.jsmith.java.byteforge.listener.BroadcastListener;

public interface Workspace {

	String getName( );
	
	List< Container > getContainers( );
	
	void openContainerAtPath( Path containerPath );
	
	void close( );
	
	CompletableFuture< List< Type > > resolveReference( Reference reference );
	
	EventBus getEventBus( );
}
