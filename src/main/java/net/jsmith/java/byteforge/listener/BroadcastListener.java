package net.jsmith.java.byteforge.listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class BroadcastListener< E > implements Listener< E > {

	private final Set< Listener< E > > listeners;
	
	public BroadcastListener( ) {
		this.listeners = new LinkedHashSet< >( );
	}

	public void register( Listener< E > listener ) {
		synchronized( this.listeners ) {
			this.listeners.add( listener );
		}
	}

	public void unregister( Listener< E > listener ) {
		synchronized( this.listeners ) {
			this.listeners.remove( listener );
		}
	}

	@Override
	public void on( E event ) {
		synchronized( this.listeners ) {
			this.listeners.stream( ).forEach( ( l ) -> {
				l.on( event );
			} );
		}
	}
	
}
