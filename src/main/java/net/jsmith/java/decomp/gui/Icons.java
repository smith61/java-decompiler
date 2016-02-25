package net.jsmith.java.decomp.gui;

import javafx.scene.image.Image;

public class Icons {
    
    public static final Image PACKAGE_ICON = Icons.loadIcon( "package.png" );
    public static final Image CLASS_ICON = Icons.loadIcon( "class.png" );
    
    private static final Image loadIcon( String name ) {
        return new Image( Icons.class.getResourceAsStream( "/icons/" + name ) );
    }
}
