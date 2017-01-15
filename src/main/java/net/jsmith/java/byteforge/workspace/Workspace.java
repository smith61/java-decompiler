package net.jsmith.java.byteforge.workspace;

import java.nio.file.Path;
import java.util.List;

import com.google.common.eventbus.EventBus;

public interface Workspace {

	String getName( );
	
	List< Container > getContainers( );
	
	void openContainerAtPath( Path containerPath );
	
	void close( );
	
	WorkspaceIndex getWorkspaceIndex( );
	
	EventBus getEventBus( );
}
