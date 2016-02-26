package net.jsmith.java.decomp.gui;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import net.jsmith.java.decomp.container.Type;
import net.jsmith.java.decomp.container.TypeContainer;

public class TypeContainerView extends BorderPane {
	
	private static final Logger LOG = LoggerFactory.getLogger( TypeContainerView.class );

    private final ContainerGroupView containerGroup;
    private final TypeContainer container;
    
    private final TypeContainerContentView contentView;
    private final TabPane typeReferenceTabs;
    
    public TypeContainerView( ContainerGroupView containerGroup, TypeContainer container ) {
        this.containerGroup = Objects.requireNonNull( containerGroup, "containerGroup" );
        this.container = Objects.requireNonNull( container, "container" );
        
        this.contentView = new TypeContainerContentView( this );
        this.contentView.setFitToHeight( true );
        
        this.typeReferenceTabs = new TabPane( );
        
        this.setLeft( this.contentView );
        this.setCenter( this.typeReferenceTabs );
    }
    
    public ContainerGroupView getContainerGroup( ) {
        return this.containerGroup;
    }
    
    public TypeContainer getTypeContainer( ) {
        return this.container;
    }
    
    public void openAndShowType( Type type ) {
        if( type.getOwningContainer( ) != this.container ) {
        	if( LOG.isErrorEnabled( ) ) {
        		LOG.error( "Attempted to open type '{}' in container '{}' into container view for '{}'.", type.getTypeMetadata( ).getFullName( ), type.getOwningContainer( ).getName( ), this.container.getName( ) );
        	}
            throw new IllegalArgumentException( "Attempted to load reference into wrong view." );
        }
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Opening and showing type '{}' in container '{}'.", type.getTypeMetadata( ).getFullName( ), type.getOwningContainer( ).getName( ) );
    	}
        
        Tab tab = this.typeReferenceTabs.getTabs( ).stream( ).filter( ( t ) -> {
            return Objects.equals( type, ( ( TypeReferenceView ) t.getContent( ) ).getType( ) );
        } ).findFirst( ).orElseGet( ( ) -> {
            Tab t = new Tab( );
            t.setText( type.getTypeMetadata( ).getTypeName( ) );
            t.setContent( new TypeReferenceView( TypeContainerView.this, type ) );
            
            typeReferenceTabs.getTabs( ).add( t );
            return t;
        } );
        
        this.typeReferenceTabs.getSelectionModel( ).select( tab );
    }
    
}
