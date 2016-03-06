package net.jsmith.java.decomp.gui.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.jsmith.java.decomp.gui.ContainerView;
import net.jsmith.java.decomp.gui.ListenerUtils;
import net.jsmith.java.decomp.gui.controls.PackageTreeItem;
import net.jsmith.java.decomp.gui.controls.SortedTreeItem;
import net.jsmith.java.decomp.gui.controls.TypeTreeItem;
import net.jsmith.java.decomp.utils.TypeNameUtils;
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
	private TreeView< String > contentTree;
	
	private ContentViewController( ContainerView containerView ) {
		this.containerView = Objects.requireNonNull( containerView, "containerView" );
		
	}
	
	private void openTypeView( Type type ) {
		if( LOG.isDebugEnabled( ) ) {
    		LOG.debug( "Recieved click event on ClassTreeItem for type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
    	}
    	containerView.openAndShowType( type );
	}
	
	private SortedTreeItem getPackageTreeItem( Metadata metadata ) {
		SortedTreeItem node = ( SortedTreeItem ) this.contentTree.getRoot( );
        for( String pkgPart : TypeNameUtils.getPackageParts( metadata.getFullName( ) ) ) {
        	SortedTreeItem nextNode = null;
            for( TreeItem< String > child : node.getChildren( ) ) {
                if( child.getValue( ).equals( pkgPart ) ) {
                    nextNode = ( SortedTreeItem ) child;
                    break;
                }
            }
            if( nextNode == null ) {
                nextNode = new PackageTreeItem( pkgPart );
                node.addSortedChild( nextNode );
            }
            node = nextNode;
        }
        
        return node;
    }
    
    private SortedTreeItem findEnclosingTreeItem( Metadata metadata ) {
    	String enclosingTypeName = TypeNameUtils.getEnclosingTypeName( metadata.getFullName( ) );
    	if( enclosingTypeName == null ) {
    		return null;
    	}
    	SortedTreeItem node = this.getPackageTreeItem( metadata );
    	for( String namePart : TypeNameUtils.getTypeParts( enclosingTypeName ) ) {
    		SortedTreeItem nextNode = null;
    		for( TreeItem< String > child : node.getChildren( ) ) {
    			if( child.getValue( ).equals( namePart ) ) {
    				nextNode = ( SortedTreeItem ) child;
    				break;
    			}
    		}
    		if( nextNode == null ) {
    			return null;
    		}
    		node = nextNode;
    	}
    	
    	return node;
    }
    
    private void addType( Type type ) {
    	Metadata metadata = type.getMetadata( );
    	if( LOG.isTraceEnabled( ) ) {
    		LOG.trace( "Recieved type loaded event for type '{}' from container '{}'.", metadata.getFullName( ), type.getContainer( ).getName( ) );
    	}
    	if( type.getMetadata( ).getEnclosingType( ) != null ) {
    		if( LOG.isTraceEnabled( ) ) {
    			LOG.trace( "Ignoring anonymous inner class '{}' in container '{}'.", metadata.getFullName( ), type.getContainer( ).getName( ) );
    		}
    		return;
    	}
    	String typeName = metadata.getTypeName( );

		// Attempt to resolve a parent type that may already
		//  be loaded. Some java programmers decide to use '$'
		//  into class names so we may not find them.
    	SortedTreeItem enclosingItem = this.findEnclosingTreeItem( metadata );
    	if( enclosingItem != null && enclosingItem instanceof TypeTreeItem ) {
    		TypeTreeItem tti = ( TypeTreeItem ) enclosingItem;
    		
    		typeName = typeName.substring( tti.getType( ).getMetadata( ).getTypeName( ).length( ) + 1 );
    		enclosingItem.addSortedChild( new TypeTreeItem( typeName, type ) );
    	}
    	else {
    		getPackageTreeItem( metadata ).addSortedChild( new TypeTreeItem( typeName, type ) );
    	}
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
