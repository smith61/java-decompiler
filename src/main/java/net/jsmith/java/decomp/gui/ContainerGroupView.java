package net.jsmith.java.decomp.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jsmith.java.decomp.container.GroupReferenceResolver;
import net.jsmith.java.decomp.container.ReferenceResolver;
import net.jsmith.java.decomp.container.Type;
import net.jsmith.java.decomp.container.TypeContainer;
import net.jsmith.java.decomp.container.TypeContainerUtils;

public class ContainerGroupView extends ScrollPane {

    private final TabPane containersTab;
    
    private final GroupReferenceResolver referenceResolver;
    
    public ContainerGroupView( ) {
        this.containersTab = new TabPane( );
        this.setContent( this.containersTab );
        
        this.referenceResolver = new GroupReferenceResolver( );
        
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
    
    public void openAndShowType( Type type ) {
        Tab tab = this.containersTab.getTabs( ).stream( ).filter( ( t ) -> {
            TypeContainerView view = ( TypeContainerView ) t.getContent( );
            return view.getTypeContainer( ) == type.getOwningContainer( );
        } ).findFirst( ).orElseThrow( ( ) -> {
            throw new IllegalArgumentException( "Unable to locate container view for container: " + type.getOwningContainer( ).getName( ) );
        } );
        
        this.containersTab.getSelectionModel( ).select( tab );
        ( ( TypeContainerView ) tab.getContent( ) ).openAndShowType( type );
    }
    
    public void openAndShowFile( File file ) {
    	TypeContainerUtils.createTypeContainerFromJar( Paths.get( file.toURI( ) ) ).whenCompleteAsync( ( container, err ) -> {
    		if( err != null ) {
    			ErrorDialog.displayError( "Error loading type container", "Error loading type container from file: " + file, err );
    		}
    		else {
    			Optional< Tab > tab = containersTab.getTabs( ).stream( ).filter( ( t ) -> {
    				TypeContainerView view = ( TypeContainerView ) t.getContent( );
    				return view.getTypeContainer( ).getName( ).equals( container.getName( ) );
    			} ).findFirst( );
    			if( tab.isPresent( ) ) {
    				try {
    					container.close( );
    				}
    				catch( IOException ioe ) { }
    				containersTab.getSelectionModel( ).select( tab.get( ) );
    			}
    			else {
    				Tab t = new Tab( );
    				t.setText( container.getName( ) );
    				t.setContent( new TypeContainerView( this, container ) );
    				
    				containersTab.getTabs( ).add( t );
    				containersTab.getSelectionModel( ).select( t );
    			}
    		}
    	}, PlatformExecutor.INSTANCE );
    }
    
}
