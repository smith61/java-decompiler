package net.jsmith.java.decomp.container;

import java.lang.ref.SoftReference;
import java.util.Objects;

import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;

import javafx.application.Platform;

public class Type {

	private final AbstractTypeContainer owningContainer;
	private final TypeMetadata typeMetadata;
	
	private final Object LOCK = new Object( );
	private SoftReference< TypeDefinition > typeDefinition;
	
	public Type( AbstractTypeContainer owningContainer, TypeMetadata typeMetadata ) {
		this.owningContainer = Objects.requireNonNull( owningContainer, "owningContainer" );
		this.typeMetadata = Objects.requireNonNull( typeMetadata, "typeMetadata" );
		
		this.typeDefinition = new SoftReference< >( null );
	}

	public TypeContainer getOwningContainer( ) {
		return owningContainer;
	}

	public TypeMetadata getTypeMetadata( ) {
		return this.typeMetadata;
	}
	
	public TypeDefinition getTypeDefinition( ) {
		if( Platform.isFxApplicationThread( ) ) {
			throw new IllegalStateException( "Type.getTypeDefinition should not be called from the FX Thread." );
		}
		
		TypeDefinition def = this.typeDefinition.get( );
		if( def == null ) {
			synchronized( this.LOCK ) {
				def = this.typeDefinition.get( );
				if( def == null ) {
					MetadataSystem metadataSystem = this.owningContainer.getMetadataSystem( );
					def = metadataSystem.lookupType( this.getTypeMetadata( ).getFullName( ) ).resolve( );
					
					this.typeDefinition = new SoftReference< >( def );
				}
			}
		}
		return def;
	}
	
}
