package net.jsmith.java.decomp.gui;

import java.util.Objects;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import net.jsmith.java.decomp.reference.TypeContainer;
import net.jsmith.java.decomp.reference.TypeReference;

public class TypeContainerContentView extends ScrollPane {

    private final TypeContainerView containerView;
    
    private final TreeView< String > contentTree;
    
    public TypeContainerContentView( TypeContainerView containerView ) {
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        
        TypeContainer typeContainer = containerView.getTypeContainer( );
        this.contentTree = new TreeView< >( new PackageTreeItem( "" ) );
        this.contentTree.setShowRoot( false );
        
        this.setContent( this.contentTree );
        
        typeContainer.getContainedTypes( ).stream( ).forEach( ( typeReference ) -> {
            getPackageTreeItem( typeReference ).getChildren( ).add( new ClassTreeItem( typeReference ) );
        } );
        ( ( PackageTreeItem ) this.contentTree.getRoot( ) ).sortChildren( );
        
        this.contentTree.setCellFactory( ( tree ) -> {
        	TreeCell< String > cell = new TextFieldTreeCell< >( );
        	cell.setOnMouseClicked( ( evt ) -> {
        		if( evt.getButton( ) == MouseButton.PRIMARY ) {
        			TreeItem< String > selectedItem = contentTree.getSelectionModel( ).getSelectedItem( );
	                if( selectedItem instanceof ClassTreeItem ) {
	                	containerView.openAndShowType( ( ( ClassTreeItem ) selectedItem ).typeReference );
	                }
        		}
        	} );
        	
        	return cell;
        } );
        this.contentTree.setOnKeyPressed( ( evt ) -> {
            if( evt.getCode( ) == KeyCode.ENTER ) {
                TreeItem< String > selectedItem = contentTree.getSelectionModel( ).getSelectedItem( );
                if( selectedItem instanceof ClassTreeItem ) {
                    containerView.openAndShowType( ( ( ClassTreeItem ) selectedItem ).typeReference );
                }
                else {
                    selectedItem.setExpanded( !selectedItem.isExpanded( ) );
                }
            }
        } );
    }
    
    public TypeContainerView getContainerView( ) {
        return this.containerView;
    }
    
    private TreeItem< String > getPackageTreeItem( TypeReference typeReference ) {
        TreeItem< String > node = this.contentTree.getRoot( );
        for( String pkgPart : typeReference.getPackageName( ).split( "\\." ) ) {
            if( pkgPart.isEmpty( ) ) {
                continue;
            }
            TreeItem< String > nextNode = null;
            for( TreeItem< String > child : node.getChildren( ) ) {
                if( child.getValue( ).equals( pkgPart ) ) {
                    nextNode = child;
                    break;
                }
            }
            if( nextNode == null ) {
                nextNode = new PackageTreeItem( pkgPart );
                node.getChildren( ).add( nextNode );
            }
            node = nextNode;
        }
        
        return node;
    }
    
    private class PackageTreeItem extends TreeItem< String > implements Comparable< TreeItem< String > > {

        public PackageTreeItem( String pkgName ) {
            super( pkgName );
            
            this.setGraphic( new ImageView( Icons.PACKAGE_ICON ) );
        }
        
        public void sortChildren( ) {
            this.getChildren( ).sort( null );
            
            this.getChildren( ).stream( ).filter( ( child ) -> {
                return child instanceof PackageTreeItem;
            } ).forEach( ( child ) -> {
                ( ( PackageTreeItem ) child ).sortChildren( );
            } );
        }
        
        @Override
        public int compareTo( TreeItem< String > o ) {
            if( o instanceof PackageTreeItem ) {
                return this.getValue( ).compareTo( o.getValue( ) );
            }
            // Only other type is ClassTreeItem, we always sort before them.
            return -1;
        }
        
    }
    
    private class ClassTreeItem extends TreeItem< String > implements Comparable< TreeItem< String > > {
        
        private final TypeReference typeReference;
        
        public ClassTreeItem( TypeReference typeReference ) {
            super( typeReference.getClassName( ) );
            
            this.typeReference = typeReference;
            
            this.setGraphic( new ImageView( Icons.CLASS_ICON ) );
        }

        @Override
        public int compareTo( TreeItem< String > o ) {
            if( o instanceof ClassTreeItem ) {
                return this.getValue( ).compareTo( o.getValue( ) );
            }
            // Only other type is PackageTreeItem, we always sort after them
            return 1;
        }
        
    }
    
}
