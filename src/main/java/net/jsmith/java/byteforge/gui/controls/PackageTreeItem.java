package net.jsmith.java.byteforge.gui.controls;

import javafx.scene.image.ImageView;
import net.jsmith.java.byteforge.gui.Icons;

public class PackageTreeItem extends SortedTreeItem {
	
	public PackageTreeItem( ) {
		this( "" );
	}
	
	public PackageTreeItem( String pkgName ) {
		super( pkgName );
		
		this.setGraphic( new ImageView( Icons.PACKAGE_ICON ) );
	}
	
}
