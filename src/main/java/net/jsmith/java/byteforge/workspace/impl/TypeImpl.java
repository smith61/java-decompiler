package net.jsmith.java.byteforge.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import net.jsmith.java.byteforge.workspace.Container;
import net.jsmith.java.byteforge.workspace.Metadata;
import net.jsmith.java.byteforge.workspace.Type;

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
	public InputStream getInputStream( ) throws IOException {
		try {
			return this.owningContainer.withReference( ( ) -> {
				try {
					InputStream is = this.owningContainer.getInputStream( this.metadata.getFullName( ) );
					return new ContainerInputStream( this.owningContainer, is );
				}
				catch( IOException ioe ) {
					throw new RuntimeException( ioe );
				}
			} );
		}
		catch( RuntimeException re ) {
			if( re.getCause( ) instanceof IOException ) {
				throw ( IOException ) re.getCause( );
			}
			throw re;
		}
	}

}
