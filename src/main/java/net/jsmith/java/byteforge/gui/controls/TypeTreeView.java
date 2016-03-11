package net.jsmith.java.byteforge.gui.controls;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.jsmith.java.byteforge.utils.TypeNameUtils;
import net.jsmith.java.byteforge.workspace.Metadata;
import net.jsmith.java.byteforge.workspace.Type;

public class TypeTreeView extends TreeView< String > {
	
	public TypeTreeView( ) {
		this.setRoot( new PackageTreeItem( "" ) );
		this.setShowRoot( false );
	}
	
	public void addType( Type type ) {
		Metadata metadata = type.getMetadata( );
		String typeName = metadata.getTypeName( );
		
		// Attempt to resolve a parent type that may already
		// be loaded. Some java programmers decide to use '$'
		// into class names so we may not find them.
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
	
	private SortedTreeItem getPackageTreeItem( Metadata metadata ) {
		SortedTreeItem node = ( SortedTreeItem ) this.getRoot( );
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
	
}
