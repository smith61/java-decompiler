package net.jsmith.java.decomp.container;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.jsmith.java.decomp.gui.PlatformExecutor;

public abstract class AbstractTypeContainer implements TypeContainer, ITypeLoader {

	private final String name;
	
	private final MetadataSystem metadataSystem;
	
	private final ObservableMap< String, Type > containedTypes;
	
	protected AbstractTypeContainer( String name ) {
		this.name = name;
		
		this.metadataSystem = new MetadataSystem( this );
		this.metadataSystem.setEagerMethodLoadingEnabled( false );
		
		this.containedTypes = FXCollections.observableHashMap( );
	}
	
	protected final MetadataSystem getMetadataSystem( ) {
		return this.metadataSystem;
	}
	
	protected final void loadTypeDefinition( String typeName ) {
		TypeReference typeReference = this.metadataSystem.lookupType( typeName );
		if( typeReference == null ) {
			// TODO: Log/Throw error?
			return;
		}
		TypeDefinition typeDefinition = typeReference.resolve( );
		if( typeDefinition == null ) {
			// TODO: Log/Throw error?
			return;
		}
		PlatformExecutor.INSTANCE.execute( ( ) -> {
			containedTypes.put( typeName, new Type( this, typeDefinition ) );
		} );
	}
	
	@Override
	public final String getName( ) {
		return this.name;
	}
	
	@Override
	public final List< Type > resolveType( String typeName ) {
		if( this.containedTypes.containsKey( typeName ) ) {
			return Arrays.asList( this.containedTypes.get( typeName ) );
		}
		return Collections.emptyList( );
	}
	
	@Override
	public final ObservableMap< String, Type > getContainedTypes( ) {
		return FXCollections.unmodifiableObservableMap( this.containedTypes );
	}
	
}
