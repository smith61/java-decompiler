package net.jsmith.java.byteforge.gui;

import java.util.ArrayList;
import java.util.List;

import net.jsmith.java.byteforge.listener.Listener;
import net.jsmith.java.byteforge.utils.ThreadPools;

public class ListenerUtils {

	public static < T > Listener< T > onFXThread( Listener< T > l ) {
		List< T > pendingEvents = new ArrayList< >( );
		return ( evt ) -> {
			boolean scheduleUpdate = false;
			synchronized( pendingEvents ) {
				scheduleUpdate = pendingEvents.isEmpty( );
				pendingEvents.add( evt );
			}
			if( scheduleUpdate ) {
				ThreadPools.PLATFORM.execute( ( ) -> {
					List< T > events;
					synchronized( pendingEvents ) {
						events = new ArrayList< >( pendingEvents );
						pendingEvents.clear( );
					}
					for( T event : events ) {
						l.on( event );
					}
				} );
			}
		};
	}
	
}
