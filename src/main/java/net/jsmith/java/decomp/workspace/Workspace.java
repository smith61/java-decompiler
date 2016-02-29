package net.jsmith.java.decomp.workspace;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface Workspace {

	String getName( );
	
	List< Container > getContainers( );
	
	void openContainerAtPath( Path containerPath );
	
	void close( );
	
	void setContainerOpenedListener( Consumer< ? super Container > l );
	void setContainerClosedListener( Consumer< ? super Container > l );
	void setErrorListener( Consumer< ? super Throwable > l );
	
}
