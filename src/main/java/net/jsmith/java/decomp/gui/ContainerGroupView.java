package net.jsmith.java.decomp.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jsmith.java.decomp.reference.PoolReferenceResolver;
import net.jsmith.java.decomp.reference.ReferenceResolver;
import net.jsmith.java.decomp.reference.TypeContainer;
import net.jsmith.java.decomp.reference.TypeContainerUtils;
import net.jsmith.java.decomp.reference.TypeReference;

public class ContainerGroupView extends ScrollPane {

    private final TabPane containersTab;
    
    private final ReferenceResolver referenceResolver;
    
    public ContainerGroupView( ) {
        this.containersTab = new TabPane( );
        this.setContent( this.containersTab );
        
        this.referenceResolver = new PoolReferenceResolver( );
        
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
                
                db.getFiles( ).stream( ).forEach( this::openAndShowFile );
            }
        } );
        
        this.containersTab.getTabs( ).addListener( ( Change< ? extends Tab > c ) -> {
            while( c.next( ) ) {
                if( c.wasRemoved( ) ) {
                    for( Tab removed : c.getRemoved( ) ) {
                        TypeContainer typeContainer = ( ( TypeContainerView ) removed.getContent( ) ).getTypeContainer( );
                        try {
                            typeContainer.close( );
                        }
                        catch( IOException ioe ) {
                            ErrorDialog.displayError( "Error closing TypeContainer", "Error closing TypeContainer: " + typeContainer.getName( ), ioe );
                        }
                    }
                }
            }
        } );
    }
    
    public ReferenceResolver getReferenceResolver( ) {
        return this.referenceResolver;
    }
    
    public void openAndShowType( TypeReference type ) {
        Tab tab = this.containersTab.getTabs( ).stream( ).filter( ( t ) -> {
            TypeContainerView view = ( TypeContainerView ) t.getContent( );
            return view.getTypeContainer( ) == type.getContainer( );
        } ).findFirst( ).orElseThrow( ( ) -> {
            throw new IllegalArgumentException( "Unable to locate container view for container: " + type.getContainer( ).getName( ) );
        } );
        
        this.containersTab.getSelectionModel( ).select( tab );
        ( ( TypeContainerView ) tab.getContent( ) ).openAndShowType( type );
    }
    
    public void openAndShowFile( File file ) {
        try {
            TypeContainer container = TypeContainerUtils.createFromJar( Paths.get( file.toURI( ) ) );
            Tab tab = this.containersTab.getTabs( ).stream( ).filter( ( t ) -> {
                TypeContainerView view = ( TypeContainerView ) t.getContent( );
                return view.getTypeContainer( ).getName( ).equals( container.getName( ) );
            } ).findFirst( ).orElseGet( ( ) -> {
                Tab t = new Tab( );
                t.setText( container.getName( ) );
                t.setContent( new TypeContainerView( ContainerGroupView.this, container ) );
                
                containersTab.getTabs( ).add( t );
                return t;
            } );
            this.containersTab.getSelectionModel( ).select( tab );
        }
        catch( IOException ioe ) {
            ErrorDialog.displayError( "Error opening container.", "Error loading container at: " + file, ioe );
        }
    }
    
}
