package net.jsmith.java.decomp.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jsmith.java.decomp.utils.IOUtils;

public class ContainerInputStream extends InputStream {

	private final AbstractContainer container;
	private final InputStream delegate;
	
	private final AtomicBoolean isClosed;
	
	public ContainerInputStream( AbstractContainer container, InputStream delegate ) {
		this.container = Objects.requireNonNull( container, "container" );
		this.delegate = Objects.requireNonNull( delegate, "delegate" );
		
		this.isClosed = new AtomicBoolean( false );
		
		this.container.incReference( );
	}
	
	@Override
	public int read( ) throws IOException {
		return this.delegate.read( );
	}

	@Override
	public int read( byte[ ] b, int off, int len ) throws IOException {
		return this.delegate.read( b, off, len );
	}

	@Override
	public void close( ) throws IOException {
		if( this.isClosed.getAndSet( true ) ) {
			return;
		}
		try {
			this.delegate.close( );
		}
		finally {
			this.container.decReference( );
		}
	}
	
	@Override
	public void finalize( ) {
		IOUtils.safeClose( this );
	}
	
}
