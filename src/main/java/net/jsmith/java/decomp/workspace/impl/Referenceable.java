package net.jsmith.java.decomp.workspace.impl;

import com.strobel.functions.Supplier;

public abstract class Referenceable {

	private final Object LOCK = new Object( );
	private int numReferences = 0;
	private boolean closed = false;
	
	public void close( ) {
		boolean scheduleClose = false;
		synchronized( LOCK ) {
			if( this.closed ) {
				return;
			}
			this.closed = true;
			
			scheduleClose = this.numReferences == 0;
		}
		if( scheduleClose ) {
			this.scheduleClose( );
		}
	}
	
	public final < R > R withReference( Supplier< ? extends R > supplier ) {
		this.incReference( );
		try {
			return supplier.get( );
		}
		finally {
			this.decReference( );
		}
	}
	
	public final void incReference( ) {
		synchronized( LOCK ) {
			if( this.closed ) {
				throw new IllegalStateException( "Object is closed." );
			}
			this.numReferences += 1;
		}
	}
	
	public final void decReference( ) {
		boolean scheduleClose = false;
		synchronized( LOCK ) {
			this.numReferences -= 1;
			if( this.closed ) {
				scheduleClose = this.numReferences == 0;
			}
		}
		if( scheduleClose ) {
			this.scheduleClose( );
		}
	}
	
	protected abstract void scheduleClose( );
	
}
