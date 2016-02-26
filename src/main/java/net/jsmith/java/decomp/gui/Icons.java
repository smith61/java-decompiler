package net.jsmith.java.decomp.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

public class Icons {
	
	private static final Logger LOG = LoggerFactory.getLogger( Icons.class );
    
    public static final Image PACKAGE_ICON = Icons.loadIcon( "package.png" );
    public static final Image CLASS_ICON = Icons.loadIcon( "class.png" );
    public static final Image INTERFACE_ICON = Icons.loadIcon( "interface.png" );
    
    private static final Image loadIcon( String name ) {
    	if( LOG.isDebugEnabled( ) ) {
    		LOG.debug( "Loading icon '{}'.", name );
    	}
        return new Image( Icons.class.getResourceAsStream( "/icons/" + name ) );
    }
}
