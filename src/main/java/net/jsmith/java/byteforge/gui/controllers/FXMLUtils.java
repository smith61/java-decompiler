package net.jsmith.java.byteforge.gui.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import net.jsmith.java.byteforge.gui.ErrorDialog;

public class FXMLUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger( FXMLUtils.class );

	public static < T extends Node > T loadView( String viewName, Controller controller ) {
		String resName = "/views/" + viewName;
		try( InputStream is = FXMLUtils.class.getResourceAsStream( resName ) ) {
			FXMLLoader loader = new FXMLLoader( );
			loader.setController( controller );
			
			T node = loader.load( is );
			node.setUserData( controller );
			
			return node;
		}
		catch( IOException ioe ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading view '{}'.", viewName, ioe );
			}
			ErrorDialog.displayError( "Error loading view", "Error loading view: " + viewName, ioe );
			return null;
		}
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends Controller > T getController( Node node ) {
		while( node != null ) {
			Object data = node.getUserData( );
			if( data != null ) {
				return ( T ) data;
			}
			node = node.getParent( );
		}
		return null;
	}
	
}
