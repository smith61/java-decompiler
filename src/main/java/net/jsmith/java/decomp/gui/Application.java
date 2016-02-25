package net.jsmith.java.decomp.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
	
	public static void main( String[ ] args ) throws Throwable {
		launch( args );
	}
	
	@Override
	public void start( Stage primaryStage ) throws Exception {
		primaryStage.setTitle( "Java Decompiler" );
		
		RepositoryViewContainer root = new RepositoryViewContainer( );
		root.setFitToHeight( true );
		root.setFitToWidth( true );
		
		primaryStage.setScene( new Scene( root, 600, 480 ) );
		primaryStage.show( );
	}


}
