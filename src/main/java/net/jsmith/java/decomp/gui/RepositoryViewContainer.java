package net.jsmith.java.decomp.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jsmith.java.decomp.repository.ClassRepository;
import net.jsmith.java.decomp.repository.ClassRepositoryUtils;

public class RepositoryViewContainer extends ScrollPane {

	private final TabPane repositoryViews;
	
	public RepositoryViewContainer( ) {
		this.repositoryViews = new TabPane( );
		this.setContent( this.repositoryViews );
		
		this.setOnDragOver( ( evt ) -> {
			Dragboard db = evt.getDragboard( );
			if( db.hasFiles( ) ) {
				evt.consume( );
				
				evt.acceptTransferModes( TransferMode.ANY );
			}
		} );
		this.setOnDragDropped( ( evt ) -> {
			Dragboard db = evt.getDragboard( );
			if( db.hasFiles( ) ) {
				evt.consume( );
				
				for( File file : db.getFiles( ) ) {
					openFile( file );
				}
			}
		} );
	}
	
	public void openFile( File file ) {
		try {
			Path jarPath = Paths.get( file.toURI( ) );
			ClassRepository repo = ClassRepositoryUtils.loadFromJar( jarPath );
			
			Tab tab = new Tab( jarPath.getFileName( ).toString( ) );
			tab.setContent( new RepositoryView( repo ) );
			this.repositoryViews.getTabs( ).add( tab );
			this.repositoryViews.getSelectionModel( ).select( tab );
		}
		catch( IOException ioe ) {
			System.err.println( "Failed to load repository:" );
			ioe.printStackTrace( );
		}
	}
	
}
