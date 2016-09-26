package net.jsmith.java.byteforge.gui.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.jsmith.java.byteforge.gui.controls.TypeTreeItem;
import net.jsmith.java.byteforge.gui.controls.TypeTreeView;
import net.jsmith.java.byteforge.utils.TypeNameUtils;
import net.jsmith.java.byteforge.workspace.Container;
import net.jsmith.java.byteforge.workspace.Metadata;
import net.jsmith.java.byteforge.workspace.Reference;
import net.jsmith.java.byteforge.workspace.Type;
import net.jsmith.java.byteforge.workspace.TypeReference;

public class ContainerViewController implements Controller {
	
	private static final Logger LOG = LoggerFactory.getLogger( ContainerViewController.class );
	
	public static Tab createView( WorkspaceViewController workspaceView, Container container ) {
		ContainerViewController controller = new ContainerViewController( workspaceView, container );
		
		Tab tab = new Tab( container.getName( ) );
		tab.setContent( FXMLUtils.loadView( "ContainerView.fxml", controller ) );
		
		return tab;
	}
	
	public static ContainerViewController getController( Tab tab ) {
		return FXMLUtils.getController( tab.getContent( ) );
	}
	
	private final WorkspaceViewController workspaceView;
	private final Container container;
	
	@FXML
	private TypeTreeView contentTree;
	
	@FXML
	private TabPane typeTabs;
	
	private ContainerViewController( WorkspaceViewController workspaceView, Container container ) {
		this.workspaceView = Objects.requireNonNull( workspaceView, "workspaceView" );
		this.container = Objects.requireNonNull( container, "container" );
	}
	
	public WorkspaceViewController getWorkspaceView( ) {
		return this.workspaceView;
	}
	
	public Container getContainer( ) {
		return this.container;
	}
	
	public void openAndShowType( Type type ) {
		this.openAndShowType( type, new TypeReference( type.getMetadata( ).getFullName( ) ) );
	}
	
	public void openAndShowType( Type type, Reference reference ) {
		if( type.getContainer( ) != this.container ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Attempted to open type '{}' in container '{}' into container view for '{}'.",
						type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ),
						this.container.getName( ) );
			}
			throw new IllegalArgumentException( "Attempted to load reference into wrong view." );
		}
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Opening and showing type '{}' in container '{}'.", type.getMetadata( ).getFullName( ),
					type.getContainer( ).getName( ) );
		}
		
		Type outerType = this.findOutermostType( type );
		Tab tab = this.typeTabs.getTabs( ).stream( ).filter( ( t ) -> {
			return Objects.equals( outerType, TypeViewController.getController( t ).getType( ) );
		} ).findFirst( ).orElseGet( ( ) -> {
			Tab t = TypeViewController.createView( this, outerType );
			
			typeTabs.getTabs( ).add( t );
			return t;
		} );
		
		this.typeTabs.getSelectionModel( ).select( tab );
		TypeViewController.getController( tab ).seekToReference( reference );
	}
	
	void addType( Type type ) {
		Metadata metadata = type.getMetadata( );
		if( LOG.isTraceEnabled( ) ) {
			LOG.trace( "Recieved type loaded event for type '{}' from container '{}'.", metadata.getFullName( ),
					type.getContainer( ).getName( ) );
		}
		if( metadata.getEnclosingType( ) != null ) {
			if( LOG.isTraceEnabled( ) ) {
				LOG.trace( "Ignoring anonymous inner class '{}' in container '{}'.", metadata.getFullName( ),
						type.getContainer( ).getName( ) );
			}
			return;
		}
		this.contentTree.addType( type );
	}
	
	private Type findOutermostType( Type actType ) {
		Type type = actType;
		while( true ) {
			String outerTypeName = TypeNameUtils.getEnclosingTypeName( type.getMetadata( ).getFullName( ) );
			if( outerTypeName == null )
				break;
			
			Type outerType = this.container.findType( outerTypeName );
			if( outerType == null )
				break;
			
			type = outerType;
		}
		return type;
	}
	
	@FXML
	private void initialize( ) {
		this.container.getContainedTypes( ).forEach( this::addType );
	}
	
	@FXML
	private void onContentTreeKeyPress( KeyEvent evt ) {
		if( evt.getCode( ) == KeyCode.ENTER ) {
			TreeItem< String > selectedItem = contentTree.getSelectionModel( ).getSelectedItem( );
			if( selectedItem instanceof TypeTreeItem ) {
				this.openAndShowType( ( ( TypeTreeItem ) selectedItem ).getType( ) );
			}
			else {
				selectedItem.setExpanded( !selectedItem.isExpanded( ) );
			}
		}
	}
	
	@FXML
	@SuppressWarnings( "unchecked" )
	private void onContentTreeMouseClick( MouseEvent evt ) {
		EventTarget target = evt.getTarget( );
		while( !( target instanceof TreeCell ) ) {
			target = ( ( Node ) target ).getParent( );
		}
		TreeCell< String > cell = ( TreeCell< String > ) target;
		if( cell.getTreeItem( ) instanceof TypeTreeItem ) {
			this.openAndShowType( ( ( TypeTreeItem ) cell.getTreeItem( ) ).getType( ) );
		}
	}
	
}
