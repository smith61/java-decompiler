package net.jsmith.java.decomp.gui;

import java.util.Objects;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import net.jsmith.java.decomp.reference.TypeContainer;
import net.jsmith.java.decomp.reference.TypeReference;

public class TypeContainerView extends BorderPane {

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
    
    public void openAndShowType( TypeReference typeReference ) {
        if( typeReference.getContainer( ) != this.container ) {
            throw new IllegalArgumentException( "Attempted to load reference into wrong view." );
        }
        
        Tab tab = this.typeReferenceTabs.getTabs( ).stream( ).filter( ( t ) -> {
            return Objects.equals( typeReference, ( ( TypeReferenceView ) t.getContent( ) ).getTypeReference( ) );
        } ).findFirst( ).orElseGet( ( ) -> {
            Tab t = new Tab( );
            t.setText( typeReference.getFullyQualifiedName( ) );
            t.setContent( new TypeReferenceView( TypeContainerView.this, typeReference ) );
            
            typeReferenceTabs.getTabs( ).add( t );
            return t;
        } );
        
        this.typeReferenceTabs.getSelectionModel( ).select( tab );
    }
    
}
