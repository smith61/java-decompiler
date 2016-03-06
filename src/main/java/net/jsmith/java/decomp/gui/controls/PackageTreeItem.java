package net.jsmith.java.decomp.gui.controls;

import javafx.scene.image.ImageView;
import net.jsmith.java.decomp.gui.Icons;

public class PackageTreeItem extends SortedTreeItem {
	
	public PackageTreeItem( ) {
		this( "" );
	}
	
	public PackageTreeItem( String pkgName ) {
		super( pkgName );
		
		this.setGraphic( new ImageView( Icons.PACKAGE_ICON ) );
	}
	
}
