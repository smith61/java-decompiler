package net.jsmith.java.byteforge.gui.controllers;

import java.io.File;
import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ApplicationMenuController implements Controller {
	
	public static MenuBar createView( WorkspaceViewController workspaceController ) {
		ApplicationMenuController controller = new ApplicationMenuController( workspaceController );
		
		return FXMLUtils.loadView( "ApplicationMenu.fxml", controller );
	}
	
	public static ApplicationMenuController getController( MenuBar menubar ) {
		return FXMLUtils.getController( menubar );
	}
	
	private final WorkspaceViewController workspaceController;
	
	@FXML
	private MenuBar applicationMenu;
	
	private ApplicationMenuController( WorkspaceViewController workspaceController ) {
		this.workspaceController = Objects.requireNonNull( workspaceController, "workspaceController" );
	}
	
	@FXML
	private void openFileBrowse( ActionEvent evt ) {
		FileChooser fChooser = new FileChooser( );
		fChooser.getExtensionFilters( ).add( new ExtensionFilter( "Jar File", "*.jar" ) );
		
		List< File > selected = fChooser.showOpenMultipleDialog( this.applicationMenu.getScene( ).getWindow( ) );
		if( selected != null ) {
			selected.stream( ).forEach( ( file ) -> {
				this.workspaceController.openAndShowFile( file );
			} );
		}
	}
	
	@FXML
	private void exitApplication( ActionEvent evt ) {
		Platform.exit( );
	}
	
}
