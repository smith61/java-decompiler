package net.jsmith.java.decomp.gui;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.jsmith.java.decomp.decompiler.DecompilerUtils;
import net.jsmith.java.decomp.workspace.Type;

public class TypeView extends BorderPane {

	private static final Logger LOG = LoggerFactory.getLogger( TypeView.class );
	private static final String TYPE_STYLESHEET = TypeView.class.getResource( "/css/type.css" ).toString( );
	
    private final ContainerView containerView;
    private final Type type;
    
    private final WebView contentView;
    
    public TypeView( ContainerView containerView, Type type ) {
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        this.type = Objects.requireNonNull( type, "type" );
        
        this.contentView = new WebView( );
        this.setCenter( this.contentView );
        
        WebEngine engine = this.contentView.getEngine( );
    	engine.setUserStyleSheetLocation( TYPE_STYLESHEET );
        engine.loadContent( "Loading..." );
        
        engine.documentProperty( ).addListener( ( obs, oldVal, newVal ) -> {
        	if( newVal != null ) {
        		registerEventHandlers( newVal );
        	}
        } );
        
        if( LOG.isInfoEnabled( ) ) {
        	LOG.info( "Decompiling type '{}' from container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
        }
        DecompilerUtils.defaultDecompile( type ).whenCompleteAsync( ( html, err ) -> {
    		if( err != null ) {
    			if( LOG.isErrorEnabled( ) ) {
    				LOG.error( "Error decompiling type '{}' from container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ), err );
    			}
                ErrorDialog.displayError( "Error decompiling type", "Error decompiling type: " + type.getMetadata( ).getFullName( ), err );
            }
            else {
            	if( LOG.isInfoEnabled( ) ) {
            		LOG.info( "Received html for type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
            	}
            	engine.loadContent( html );
            }
    	}, PlatformExecutor.INSTANCE );
    }
    
    public ContainerView getContainerView( ) {
        return this.containerView;
    }
    
    public Type getType( ) {
        return this.type;
    }
    
    private void registerEventHandlers( Document document ) {
    	NodeList spans = document.getElementsByTagName( "span" );
    	for( int i = 0; i < spans.getLength( ); i++ ) {
    		Node span = spans.item( i );
    		
    		NamedNodeMap attribs = span.getAttributes( );
    		Node refTypeNode = attribs.getNamedItem( "ref_type" );
    		if( refTypeNode == null ) continue;
    		
    		( ( EventTarget ) span ).addEventListener( "click", ( evt ) -> {
    			String refType = refTypeNode.getTextContent( );
    			if( refType.equals( "type" ) ) {
    				String typeName = attribs.getNamedItem( "type" ).getTextContent( );
    				this.handleTypeReference( typeName );
    			}
    			else {
    				LOG.warn( "Unhandled reference type '{}'.", refType );
    			}
    		}, true );
    	}
    }
    
    private void handleTypeReference( String typeName ) {
//    	if( LOG.isInfoEnabled( ) ) {
//    		LOG.info( "Handling type reference for type '{}'.", typeName );
//    	}
    	
    }
    
}
