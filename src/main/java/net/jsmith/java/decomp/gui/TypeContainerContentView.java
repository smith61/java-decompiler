package net.jsmith.java.decomp.gui;

import java.util.ListIterator;
import java.util.Objects;

import javafx.collections.MapChangeListener.Change;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import net.jsmith.java.decomp.container.Type;
import net.jsmith.java.decomp.container.TypeContainer;

public class TypeContainerContentView extends ScrollPane {

    private final TypeContainerView containerView;
    
    private final TreeView< String > contentTree;
    
    public TypeContainerContentView( TypeContainerView containerView ) {
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        
        TypeContainer typeContainer = containerView.getTypeContainer( );
        this.contentTree = new TreeView< >( new PackageTreeItem( "" ) );
        this.contentTree.setShowRoot( false );
        
        this.setContent( this.contentTree );
        
        typeContainer.getContainedTypes( ).addListener( ( Change< ? extends String, ? extends Type > change ) -> {
        	if( change.wasAdded( ) ) {
            	Type type = change.getValueAdded( );
         
            	getPackageTreeItem( type ).addChildSorted( new ClassTreeItem( type, type.getTypeDefinition( ).getName( ) ) );
        	}
        } );
        buildContentTree( );
        
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
    
    private SortableTreeItem getPackageTreeItem( Type typeReference ) {
        SortableTreeItem node = ( SortableTreeItem ) this.contentTree.getRoot( );
        for( String pkgPart : typeReference.getTypeDefinition( ).getPackageName( ).split( "\\." ) ) {
            if( pkgPart.isEmpty( ) ) {
                continue;
            }
            SortableTreeItem nextNode = null;
            for( TreeItem< String > child : node.getChildren( ) ) {
                if( child.getValue( ).equals( pkgPart ) ) {
                    nextNode = ( SortableTreeItem ) child;
                    break;
                }
            }
            if( nextNode == null ) {
                nextNode = new PackageTreeItem( pkgPart );
                node.addChildSorted( nextNode );
            }
            node = nextNode;
        }
        
        return node;
    }
    
    private void buildContentTree( ) {
    	TypeContainer container = this.containerView.getTypeContainer( );
    	
    	this.contentTree.setRoot( new PackageTreeItem( container.getName( ) ) );
    	container.getContainedTypes( ).values( ).stream( ).forEach( ( type ) -> {
    		getPackageTreeItem( type ).addChildSorted( new ClassTreeItem( type, type.getTypeDefinition( ).getName( ) ) );
    	} );
    }
    
    private abstract class SortableTreeItem extends TreeItem< String > implements Comparable< TreeItem< String > > {

    	public SortableTreeItem( String value ) {
    		super( value );
    	}
    	
    	public void addChildSorted( SortableTreeItem item ) {
    		ListIterator< TreeItem< String > > itr = this.getChildren( ).listIterator( );
    		while( itr.hasNext( ) ) {
    			TreeItem< String > child = itr.next( );
    			
    			int cmp = item.compareTo( child );
    			if( cmp < 0 ) {
    				itr.previous( );
    				break;
    			}
    		}
    		itr.add( item );
    	}
    	
		@Override
		public int compareTo( TreeItem< String > o ) {
			if( this instanceof PackageTreeItem ) {
				if( o instanceof PackageTreeItem ) {
					return this.getValue( ).compareTo( o.getValue( ) );
				}
				else {
					// ClassTreeItems are sorted after us
					return -1;
				}
			}
			else if( o instanceof PackageTreeItem ) {
				// We are a ClassTreeItem so we sort after PackageTreeItems
				return 1;
			}
			else {
				return this.getValue( ).compareTo( o.getValue( ) );
			}
		}
    	
    }
    
    private class PackageTreeItem extends SortableTreeItem {

        public PackageTreeItem( String pkgName ) {
            super( pkgName );
            
            this.setGraphic( new ImageView( Icons.PACKAGE_ICON ) );
        }
        
    }
    
    private class ClassTreeItem extends SortableTreeItem {
        
        private final Type typeReference;
        
        public ClassTreeItem( Type typeReference, String className ) {
            super( className );
            
            this.typeReference = typeReference;
            
            this.setGraphic( new ImageView( Icons.CLASS_ICON ) );
        }
        
    }
    
}
