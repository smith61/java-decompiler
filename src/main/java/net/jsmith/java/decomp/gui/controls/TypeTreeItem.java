package net.jsmith.java.decomp.gui.controls;

import java.util.Objects;

import javafx.scene.image.ImageView;
import net.jsmith.java.decomp.gui.Icons;
import net.jsmith.java.decomp.workspace.Metadata;
import net.jsmith.java.decomp.workspace.Modifier;
import net.jsmith.java.decomp.workspace.Type;

public class TypeTreeItem extends SortedTreeItem {
	
	private final Type type;
	
	public TypeTreeItem( String name, Type type ) {
		super( name );
		
		this.type = Objects.requireNonNull( type, "type" );
        Metadata metadata = type.getMetadata( );
        if( Modifier.isInterface( metadata.getModifiers( ) ) ) {
        	this.setGraphic( new ImageView( Icons.INTERFACE_ICON ) );
        }
        else {
        	this.setGraphic( new ImageView( Icons.CLASS_ICON ) );
        }
	}
	
	public Type getType( ) {
		return this.type;
	}
	
}
