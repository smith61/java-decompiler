package net.jsmith.java.decomp.gui.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.jsmith.java.decomp.gui.ContainerView;
import net.jsmith.java.decomp.gui.ListenerUtils;
import net.jsmith.java.decomp.gui.controls.TypeTreeItem;
import net.jsmith.java.decomp.gui.controls.TypeTreeView;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Metadata;
import net.jsmith.java.decomp.workspace.Type;

public class ContentViewController implements Controller {
	
	private static final Logger LOG = LoggerFactory.getLogger( ContentViewController.class );
	
	public static Node createView( ContainerView containerView ) {
		ContentViewController controller = new ContentViewController( containerView );
		
		return FXMLUtils.loadView( "ContentView.fxml", controller );
	}
	
	public static ContentViewController getController( Node node ) {
		return FXMLUtils.getController( node );
	}
	
	private final ContainerView containerView;
	
	@FXML
	private TypeTreeView contentTree;
	
	private ContentViewController( ContainerView containerView ) {
		this.containerView = Objects.requireNonNull( containerView, "containerView" );
		
	}
	
	private void openTypeView( Type type ) {
		if( LOG.isDebugEnabled( ) ) {
    		LOG.debug( "Recieved click event on ClassTreeItem for type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
    	}
    	containerView.openAndShowType( type );
	}
    
    private void addType( Type type ) {
    	Metadata metadata = type.getMetadata( );
    	if( LOG.isTraceEnabled( ) ) {
    		LOG.trace( "Recieved type loaded event for type '{}' from container '{}'.", metadata.getFullName( ), type.getContainer( ).getName( ) );
    	}
    	if( metadata.getEnclosingType( ) != null ) {
    		if( LOG.isTraceEnabled( ) ) {
    			LOG.trace( "Ignoring anonymous inner class '{}' in container '{}'.", metadata.getFullName( ), type.getContainer( ).getName( ) );
    		}
    		return;
    	}
    	this.contentTree.addType( type );
    }
	
	@FXML
	private void initialize( ) {
		Container container = this.containerView.getContainer( );
        container.onTypeLoaded( ).register( ListenerUtils.onFXThread( this::addType ) );
        container.getContainedTypes( ).forEach( this::addType );
	}

	@FXML
	@SuppressWarnings( "unchecked" )
	private void onTreeItemClicked( MouseEvent evt ) {
		EventTarget target = evt.getTarget( );
		while( !( target instanceof TreeCell ) ) {
			target = ( ( Node ) target ).getParent( );
		}
		TreeCell< String > cell = ( TreeCell< String > ) target;
		if( cell.getTreeItem( ) instanceof TypeTreeItem ) {
			this.openTypeView( ( ( TypeTreeItem ) cell.getTreeItem( ) ).getType( ) );
		}
	}
	
	@FXML
	private void onKeyPressed( KeyEvent evt ) {
		if( evt.getCode( ) == KeyCode.ENTER ) {
            TreeItem< String > selectedItem = contentTree.getSelectionModel( ).getSelectedItem( );
            if( selectedItem instanceof TypeTreeItem ) {
            	this.openTypeView( ( ( TypeTreeItem ) selectedItem ).getType( ) );
            	
            }
            else {
                selectedItem.setExpanded( !selectedItem.isExpanded( ) );
            }
        }
	}
	
}
