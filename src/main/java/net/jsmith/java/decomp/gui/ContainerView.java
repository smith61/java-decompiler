package net.jsmith.java.decomp.gui;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import net.jsmith.java.decomp.gui.controllers.TypeViewController;
import net.jsmith.java.decomp.utils.TypeNameUtils;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Reference;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.TypeReference;

public class ContainerView extends BorderPane {
	
	private static final Logger LOG = LoggerFactory.getLogger( ContainerView.class );

    private final WorkspaceView workspaceView;
    private final Container container;
    
    private final ContainerContentView contentView;
    private final TabPane typeReferenceTabs;
    
    public ContainerView( WorkspaceView containerGroup, Container container ) {
        this.workspaceView = Objects.requireNonNull( containerGroup, "containerGroup" );
        this.container = Objects.requireNonNull( container, "container" );
        
        this.contentView = new ContainerContentView( this );
        this.contentView.setFitToHeight( true );
        
        this.typeReferenceTabs = new TabPane( );
        
        this.setLeft( this.contentView );
        this.setCenter( this.typeReferenceTabs );
    }
    
    public WorkspaceView getWorkspaceView( ) {
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
        		LOG.error( "Attempted to open type '{}' in container '{}' into container view for '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ), this.container.getName( ) );
        	}
            throw new IllegalArgumentException( "Attempted to load reference into wrong view." );
        }
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Opening and showing type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
    	}
    	
    	Type outerType = this.findOutermostType( type );
        Tab tab = this.typeReferenceTabs.getTabs( ).stream( ).filter( ( t ) -> {
        	return Objects.equals( outerType, TypeViewController.getController( t ).getType( ) );
        } ).findFirst( ).orElseGet( ( ) -> {
            Tab t = TypeViewController.createView( this, outerType );
            
            typeReferenceTabs.getTabs( ).add( t );
            return t;
        } );
        
        this.typeReferenceTabs.getSelectionModel( ).select( tab );
    	TypeViewController.getController( tab ).seekToReference( reference );
    }
    
    private Type findOutermostType( Type actType ) {
    	Type type = actType;
    	while( true ) {
    		String outerTypeName = TypeNameUtils.getEnclosingTypeName( type.getMetadata( ).getFullName( ) );
    		if( outerTypeName == null ) break;
    		
    		Type outerType = this.container.findType( outerTypeName );
    		if( outerType == null ) break;
    		
    		type = outerType;
    	}
    	return type;
    }
    
}
