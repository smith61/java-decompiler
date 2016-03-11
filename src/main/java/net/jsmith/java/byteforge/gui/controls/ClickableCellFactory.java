package net.jsmith.java.byteforge.gui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class ClickableCellFactory implements Callback< TreeView< String >, TreeCell< String > > {

	private ObjectProperty< EventHandler< ? super MouseEvent > > onMouseClicked = new SimpleObjectProperty< >( );
	
	public void setOnMouseClicked( EventHandler< ? super MouseEvent > handler ) {
		this.onMouseClickedProperty( ).set( handler );
	}
	
	public EventHandler< ? super MouseEvent > getOnMouseClicked( ) {
		return this.onMouseClickedProperty( ).get( );
	}
	
	public ObjectProperty< EventHandler< ? super MouseEvent > > onMouseClickedProperty( ) {
		return this.onMouseClicked;
	}
	
	@Override
	public TreeCell< String > call( TreeView< String > param ) {
		TreeCell< String > cell = new TextFieldTreeCell< >( );
		cell.onMouseClickedProperty( ).bind( this.onMouseClickedProperty( ) );
		
		return cell;
	}
	
}
