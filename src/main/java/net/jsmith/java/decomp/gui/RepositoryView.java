package net.jsmith.java.decomp.gui;

import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.java.JavaLanguage;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import net.jsmith.java.decomp.repository.ClassRepository;

public class RepositoryView extends BorderPane {

	private final ClassRepository repository;
	
	private final RepositoryContentView contentView;
	
	private final TabPane typeContentContainer;
	
	public RepositoryView( ClassRepository repository ) {
		this.repository = repository;
		
		this.contentView = new RepositoryContentView( this );
		
		this.typeContentContainer = new TabPane( );
		
		this.setLeft( this.contentView );
		this.setCenter( this.typeContentContainer );
	}
	
	public ClassRepository getClassRepository( ) {
		return this.repository;
	}
	
	public void openType( String typeName ) {
		// TODO: Offload this to background thread.
		
		for( Tab tab : this.typeContentContainer.getTabs( ) ) {
			if( tab.getText( ).contentEquals( typeName ) ) {
				this.typeContentContainer.getSelectionModel( ).select( tab );
				return;
			}
		}
		
		DecompilationOptions options = new DecompilationOptions( );
		options.getSettings( ).setTypeLoader( this.repository );
		options.getSettings( ).setForceExplicitImports( true );
		
		MetadataSystem metadata = new MetadataSystem( this.repository );
		TypeReference type = metadata.lookupType( typeName );
		TypeDefinition def = type.resolve( );
		
		JavaLanguage javaLang = new JavaLanguage( );
		
		PlainTextOutput output = new PlainTextOutput( );
		javaLang.decompileType( def, output, options );
		
		Tab tab = new Tab( typeName );
		tab.setContent( new TypeContentView( output.toString( ) ) );
		this.typeContentContainer.getTabs( ).add( tab );
		this.typeContentContainer.getSelectionModel( ).select( tab );
	}
	
}
