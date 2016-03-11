package net.jsmith.java.byteforge.workspace.impl;

import java.util.concurrent.CompletableFuture;

import com.strobel.functions.Supplier;

import net.jsmith.java.byteforge.utils.ThreadPools;

public abstract class Referenceable {

	private final Object LOCK = new Object( );
	private int numReferences = 0;
	private boolean closed = false;
	
	public void close( ) {
		boolean doClose = false;
		synchronized( LOCK ) {
			if( this.closed ) {
				return;
			}
			this.closed = true;
			
			doClose = this.numReferences == 0;
		}
		if( doClose ) {
			this.implClose( );
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
	
	public final CompletableFuture< Void > withReferenceAsync( Runnable runnable ) {
		return ThreadPools.runBackground( ( ) -> {
			this.incReference( );
			try {
				runnable.run( );
			}
			finally {
				this.decReference( );
			}
		} );
	}
	
	public final < R > CompletableFuture< R > withReferenceAsync( Supplier< ? extends R > supplier ) {
		return ThreadPools.supplyBackground( ( ) -> {
			this.incReference( );
			try {
				return supplier.get( );
			}
			finally {
				this.decReference( );
			}
		} );
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
		boolean doClose = false;
		synchronized( LOCK ) {
			this.numReferences -= 1;
			if( this.closed ) {
				doClose = this.numReferences == 0;
			}
		}
		if( doClose ) {
			this.implClose( );
		}
	}
	
	protected abstract void implClose( );
	
}
