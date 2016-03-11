package net.jsmith.java.byteforge.gui.controls;

import java.util.ListIterator;

import javafx.scene.control.TreeItem;

public abstract class SortedTreeItem extends TreeItem< String > implements Comparable< SortedTreeItem > {
	
	public SortedTreeItem( String value ) {
		super( value );
	}
	
	public void addSortedChild( SortedTreeItem item ) {
		ListIterator< TreeItem< String > > itr = this.getChildren( ).listIterator( );
		while( itr.hasNext( ) ) {
			SortedTreeItem child = ( SortedTreeItem ) itr.next( );
			
			int c = item.compareTo( child );
			if( c < 0 ) {
				itr.previous( );
				break;
			}
			else if( c == 0 ) {
				itr.remove( );
				break;
			}
		}
		itr.add( item );
	}

	@Override
	public int compareTo( SortedTreeItem o ) {
		boolean uPackage = !( this instanceof TypeTreeItem );
		boolean tPackage = !( o instanceof TypeTreeItem );
		
		if( uPackage && !tPackage ) {
			// Packages are always sorted before types
			return -1;
		}
		else if( !uPackage && tPackage ) {
			// Packages are always sorted before types
			return 1;
		}
		else {
			return this.getValue( ).compareTo( o.getValue( ) );
		}
	}
	
}
