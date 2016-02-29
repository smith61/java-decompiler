package net.jsmith.java.decomp.gui;

import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jsmith.java.decomp.workspace.Workspace;
import net.jsmith.java.decomp.workspace.impl.WorkspaceImpl;

public class Application extends javafx.application.Application {
    
    public static void main( String[ ] args ) throws Throwable {
        launch( args );
    }
    
    @Override
    public void start( Stage primaryStage ) throws Exception {
        primaryStage.setTitle( "Java Decompiler" );
        
        VBox root = new VBox( );
        
        Workspace defaultWorkspace = new WorkspaceImpl( "default" );
        WorkspaceView containerGroup = new WorkspaceView( defaultWorkspace );
        containerGroup.setFitToHeight( true );
        containerGroup.setFitToWidth( true );
        
        ApplicationMenuBar menuBar = new ApplicationMenuBar( containerGroup );
        
        root.getChildren( ).addAll( menuBar, containerGroup );
        VBox.setVgrow( containerGroup, Priority.ALWAYS );
        
        primaryStage.setScene( new Scene( root, 600, 480 ) );
        primaryStage.show( );
        
        primaryStage.setOnHidden( ( evt ) -> {
        	defaultWorkspace.close( );
        	System.gc( );
        } );
    }
    
}
