package net.jsmith.java.decomp.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListenerUtils {

	public static < T > Consumer< T > onFXThread( Consumer< T > l ) {
		List< T > pendingEvents = new ArrayList< >( );
		return ( evt ) -> {
			boolean scheduleUpdate = false;
			synchronized( pendingEvents ) {
				scheduleUpdate = pendingEvents.isEmpty( );
				pendingEvents.add( evt );
			}
			if( scheduleUpdate ) {
				PlatformExecutor.INSTANCE.execute( ( ) -> {
					List< T > events;
					synchronized( pendingEvents ) {
						events = new ArrayList< >( pendingEvents );
						pendingEvents.clear( );
					}
					for( T event : events ) {
						l.accept( event );
					}
				} );
			}
		};
	}
	
}
