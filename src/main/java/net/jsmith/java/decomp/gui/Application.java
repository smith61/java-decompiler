package net.jsmith.java.decomp.gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jsmith.java.decomp.gui.controllers.ApplicationMenuController;
import net.jsmith.java.decomp.gui.controllers.WorkspaceViewController;
import net.jsmith.java.decomp.workspace.Workspace;
import net.jsmith.java.decomp.workspace.impl.WorkspaceImpl;

public class Application extends javafx.application.Application {
    
    public static void main( String[ ] args ) throws Throwable {
        launch( args );
    }
    
    private final Workspace defaultWorkspace;
    
    public Application( ) {
    	this.defaultWorkspace = new WorkspaceImpl( "default" );
    }
    
    @Override
    public void start( Stage primaryStage ) throws Exception {
        primaryStage.setTitle( "Java Decompiler" );
        
        VBox root = new VBox( );
        
        Node workspaceView = WorkspaceViewController.createView( this.defaultWorkspace );
        MenuBar menuBar = ApplicationMenuController.createView( WorkspaceViewController.getController( workspaceView ) );
        
        root.getChildren( ).addAll( menuBar, workspaceView );
        VBox.setVgrow( workspaceView, Priority.ALWAYS );
        
        primaryStage.setScene( new Scene( root, 600, 480 ) );
        primaryStage.show( );
    }

	@Override
	public void stop( ) throws Exception {
		this.defaultWorkspace.close( );
		System.gc( );
	}
    
}
