package net.jsmith.java.decomp.listener;

import java.util.LinkedList;
import java.util.List;

public class BroadcastListener< E > implements Listener< E > {

	private final List< Listener< E > > listeners;
	
	public BroadcastListener( ) {
		this.listeners = new LinkedList< >( );
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
