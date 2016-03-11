package net.jsmith.java.byteforge.gui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class ErrorDialog {

    public static void displayError( String title, String header, Throwable err ) {
        Platform.runLater( ( ) -> {
            Alert alert = new Alert( AlertType.ERROR );
            alert.setTitle( title );
            alert.setHeaderText( header );
            alert.getDialogPane( ).setExpandableContent( ErrorDialog.errorToTextArea( err ) );
            
            alert.showAndWait( );
        } );
    }
    
    private static TextArea errorToTextArea( Throwable err ) {
        StringWriter sw = new StringWriter( );
        PrintWriter pw  = new PrintWriter( sw );
        
        err.printStackTrace( pw );
        pw.close( );
        
        TextArea errorText = new TextArea( sw.toString( ) );
        errorText.setEditable( false );
        
        return errorText;
    }
    
}
