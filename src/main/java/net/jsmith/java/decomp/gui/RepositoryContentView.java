package net.jsmith.java.decomp.gui;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import net.jsmith.java.decomp.repository.ClassRepository;

public class RepositoryContentView extends ScrollPane {

	private final TreeView< String > contextTree;
	
	public RepositoryContentView( RepositoryView context ) {
		ClassRepository repo = context.getClassRepository( );
		
		this.contextTree = new TreeView< String >( new RepositoryTreeItem( repo.getName( ), true ) );
		for( String className : repo.getResolvableTypes( ) ) {
			this.addClassNode( className );
		}
		( ( RepositoryTreeItem ) this.contextTree.getRoot( ) ).reorderChildren( );
		this.setContent( this.contextTree );
		
		this.contextTree.setOnKeyPressed( ( evt ) -> {
			if( evt.getCode( ) == KeyCode.ENTER ) {
				RepositoryTreeItem si = ( RepositoryTreeItem ) contextTree.getSelectionModel( ).getSelectedItem( );
				if( si.isPackage ) {
					si.setExpanded( !si.isExpanded( ) );
				}
				else {
					String typeName = si.getValue( );
					TreeItem< String > node = si.getParent( );
					while( node != contextTree.getRoot( ) ) {
						typeName = node.getValue( ) + '.' + typeName;
						node = node.getParent( );
					}
					context.openType( typeName );
				}
			}
		} );
		this.contextTree.setOnMouseClicked( ( evt ) -> {
			if( evt.getButton( ) == MouseButton.PRIMARY ) {
				RepositoryTreeItem si = ( RepositoryTreeItem ) contextTree.getSelectionModel( ).getSelectedItem( );
				if( !si.isPackage && evt.getClickCount( ) >= 1 ) {
					String typeName = si.getValue( );
					TreeItem< String > node = si.getParent( );
					while( node != contextTree.getRoot( ) ) {
						typeName = node.getValue( ) + '.' + typeName;
						node = node.getParent( );
					}
					context.openType( typeName );
				}
			}
		} );
	}
	
	private TreeItem< String > getPackageNode( String pkg ) {
		TreeItem< String > node = this.contextTree.getRoot( );
		for( String pkgSegment : pkg.split( "\\." ) ) {
			if( pkgSegment.isEmpty( ) ) continue;
			
			TreeItem< String > nextNode = null;
			for( TreeItem< String > child : node.getChildren( ) ) {
				if( child.getValue( ).equals( pkgSegment ) ) {
					nextNode = child;
					break;
				}
			}
			if( nextNode == null ) {
				nextNode = new RepositoryTreeItem( pkgSegment, true );
				node.getChildren( ).add( nextNode );
			}
			node = nextNode;
		}
		return node;
	}
	
	private void addClassNode( String classPath ) {
		String pkgName = this.getPackageName( classPath );
		String clsName = this.getClassName( classPath );
		if( clsName.contains( "$" ) ) return;
		
		TreeItem< String > pkgNode = this.getPackageNode( pkgName );
		pkgNode.getChildren( ).add( new RepositoryTreeItem( clsName, false ) );
	}
	
	private String getPackageName( String classPath ) {
		int endIndex = classPath.lastIndexOf( '.' );
		if( endIndex <= 0 ) {
			return "";
		}
		return classPath.substring( 0, endIndex );
	}
	
	private String getClassName( String classPath ) {
		int startIndex = classPath.lastIndexOf( '.' );
		if( startIndex <= 0 ) {
			return classPath;
		}
		return classPath.substring( startIndex + 1 );
	}
	
	private static class RepositoryTreeItem extends TreeItem< String > implements Comparable< RepositoryTreeItem > {
		
		private final boolean isPackage;
		
		public RepositoryTreeItem( String nodeName, boolean isPackage ) {
			super( nodeName );
			
			this.isPackage = isPackage;
			if( isPackage ) {
				this.setGraphic( new ImageView( Icons.PACKAGE_ICON ) );
			}
			else {
				this.setGraphic( new ImageView( Icons.CLASS_ICON ) );
			}
		}
		
		public void reorderChildren( ) {
			this.getChildren( ).sort( null );
			for( TreeItem< String > child : this.getChildren( ) ) {
				( ( RepositoryTreeItem ) child ).reorderChildren( );
			}
		}

		@Override
		public int compareTo( RepositoryTreeItem o ) {
			if( this.isPackage && !o.isPackage ) {
				return -1;
			}
			else if( !this.isPackage && o.isPackage ) {
				return 1;
			}
			return this.getValue( ).compareTo( o.getValue( ) );
		}
		
	}
	
}
