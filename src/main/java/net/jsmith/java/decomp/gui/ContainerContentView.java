package net.jsmith.java.decomp.gui;

import java.util.ListIterator;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import net.jsmith.java.decomp.utils.TypeNameUtils;
import net.jsmith.java.decomp.workspace.Metadata;
import net.jsmith.java.decomp.workspace.Modifier;
import net.jsmith.java.decomp.workspace.Type;

public class ContainerContentView extends ScrollPane {

	private static final Logger LOG = LoggerFactory.getLogger( ContainerContentView.class );
	
    private final ContainerView containerView;
    
    private final TreeView< String > contentTree;
    
    public ContainerContentView( ContainerView containerView ) {
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        
        this.contentTree = new TreeView< >( new PackageTreeItem( "" ) );
        this.contentTree.setShowRoot( false );
        
        this.setContent( this.contentTree );
        
        this.contentTree.setCellFactory( ( tree ) -> {
        	TreeCell< String > cell = new TextFieldTreeCell< >( );
        	cell.setOnMouseClicked( ( evt ) -> {
        		if( evt.getButton( ) == MouseButton.PRIMARY ) {
        			TreeItem< String > selectedItem = contentTree.getSelectionModel( ).getSelectedItem( );
	                if( selectedItem instanceof TypeTreeItem ) {
	                	Type type = ( ( TypeTreeItem ) selectedItem ).type;
	                	if( LOG.isDebugEnabled( ) ) {
	                		LOG.debug( "Recieved click event on ClassTreeItem for type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
	                	}
	                	containerView.openAndShowType( type );
	                }
        		}
        	} );
        	
        	return cell;
        } );
        this.contentTree.setOnKeyPressed( ( evt ) -> {
            if( evt.getCode( ) == KeyCode.ENTER ) {
                TreeItem< String > selectedItem = contentTree.getSelectionModel( ).getSelectedItem( );
                if( selectedItem instanceof TypeTreeItem ) {
                	Type type = ( ( TypeTreeItem ) selectedItem ).type;
                	if( LOG.isDebugEnabled( ) ) {
                		LOG.debug( "Recieved key event on ClassTreeItem for type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
                	}
                    containerView.openAndShowType( type );
                }
                else {
                    selectedItem.setExpanded( !selectedItem.isExpanded( ) );
                }
            }
        } );
        
        containerView.getContainer( ).setOnTypeLoadedListener( ListenerUtils.onFXThread( this::addType ) );
    }
    
    public ContainerView getContainerView( ) {
        return this.containerView;
    }
    
    private SortableTreeItem getPackageTreeItem( Metadata metadata ) {
        SortableTreeItem node = ( SortableTreeItem ) this.contentTree.getRoot( );
        for( String pkgPart : TypeNameUtils.getPackageParts( metadata.getFullName( ) ) ) {
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
    
    private SortableTreeItem findEnclosingTreeItem( Metadata metadata ) {
    	String enclosingTypeName = TypeNameUtils.getEnclosingTypeName( metadata.getFullName( ) );
    	if( enclosingTypeName == null ) {
    		return null;
    	}
    	SortableTreeItem node = this.getPackageTreeItem( metadata );
    	for( String namePart : TypeNameUtils.getTypeParts( enclosingTypeName ) ) {
    		SortableTreeItem nextNode = null;
    		for( TreeItem< String > child : node.getChildren( ) ) {
    			if( child.getValue( ).equals( namePart ) ) {
    				nextNode = ( SortableTreeItem ) child;
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
    	SortableTreeItem enclosingItem = this.findEnclosingTreeItem( metadata );
    	if( enclosingItem != null && enclosingItem instanceof TypeTreeItem ) {
    		TypeTreeItem tti = ( TypeTreeItem ) enclosingItem;
    		
    		typeName = typeName.substring( tti.type.getMetadata( ).getTypeName( ).length( ) + 1 );
    		enclosingItem.addChildSorted( new TypeTreeItem( type, typeName ) );
    	}
    	else {
    		getPackageTreeItem( metadata ).addChildSorted( new TypeTreeItem( type, typeName ) );
    	}
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
    
    private class TypeTreeItem extends SortableTreeItem {
        
        private final Type type;
        
        public TypeTreeItem( Type typeReference, String className ) {
            super( className );
            
            this.type = typeReference;
            
            Metadata metadata = typeReference.getMetadata( );
            if( Modifier.isInterface( metadata.getModifiers( ) ) ) {
            	this.setGraphic( new ImageView( Icons.INTERFACE_ICON ) );
            }
            else {
            	this.setGraphic( new ImageView( Icons.CLASS_ICON ) );
            }
        }
        
    }
    
}
