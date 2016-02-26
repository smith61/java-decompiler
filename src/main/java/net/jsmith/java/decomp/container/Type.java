package net.jsmith.java.decomp.container;

import java.util.Objects;

import com.strobel.assembler.metadata.TypeDefinition;

public class Type {

	private final TypeContainer owningContainer;
	private final TypeDefinition typeDefinition;
	
	public Type( TypeContainer owningContainer, TypeDefinition typeDefinition ) {
		this.owningContainer = Objects.requireNonNull( owningContainer, "owningContainer" );
		this.typeDefinition = Objects.requireNonNull( typeDefinition, "typeDefinition" );
	}

	public TypeContainer getOwningContainer( ) {
		return owningContainer;
	}

	public TypeDefinition getTypeDefinition( ) {
		return typeDefinition;
	}
	
}
