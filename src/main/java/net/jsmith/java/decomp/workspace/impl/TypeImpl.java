package net.jsmith.java.decomp.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import net.jsmith.java.decomp.utils.ThreadPools;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Metadata;
import net.jsmith.java.decomp.workspace.Type;

public class TypeImpl implements Type {

	private final AbstractContainer owningContainer;
	private final MetadataImpl metadata;
	
	public TypeImpl( AbstractContainer container, MetadataImpl metadata ) {
		this.owningContainer = Objects.requireNonNull( container, "container" );
		this.metadata = Objects.requireNonNull( metadata, "metadata" );
	}
	
	@Override
	public Container getContainer( ) {
		return this.owningContainer;
	}

	@Override
	public Metadata getMetadata( ) {
		return this.metadata;
	}

	@Override
	public CompletableFuture< InputStream > getInputStream( ) {
		return ThreadPools.supplyBackground( ( ) -> {
			this.owningContainer.incReference( );
			try {
				return this.owningContainer.getInputStream( this.metadata.getFullName( ) );
			}
			catch( IOException ioe ) {
				throw new RuntimeException( ioe );
			}
			finally {
				this.owningContainer.decReference( );
			}
		} );
	}

}
