package net.jsmith.java.byteforge.gui.controls;

import java.util.Objects;

import javafx.scene.image.ImageView;
import net.jsmith.java.byteforge.gui.Icons;
import net.jsmith.java.byteforge.workspace.Metadata;
import net.jsmith.java.byteforge.workspace.Modifier;
import net.jsmith.java.byteforge.workspace.Type;

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
