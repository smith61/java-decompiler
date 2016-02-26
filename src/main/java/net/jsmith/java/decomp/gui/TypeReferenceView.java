package net.jsmith.java.decomp.gui;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.strobel.decompiler.languages.java.ast.CompilationUnit;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.jsmith.java.decomp.container.Decompiler;
import net.jsmith.java.decomp.container.Type;

public class TypeReferenceView extends BorderPane {

	private static final Logger LOG = LoggerFactory.getLogger( TypeReferenceView.class );
	
    private final TypeContainerView containerView;
    private final Type type;
    
    private final WebView contentView;
    
    public TypeReferenceView( TypeContainerView containerView, Type type ) {
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        this.type = Objects.requireNonNull( type, "type" );
        
        this.contentView = new WebView( );
        this.setCenter( this.contentView );
        
        WebEngine engine = this.contentView.getEngine( );
        engine.loadContent( "Loading..." );
        
        if( LOG.isInfoEnabled( ) ) {
        	LOG.info( "Decompiling type '{}' from container '{}'.", type.getTypeMetadata( ).getFullName( ), type.getOwningContainer( ).getName( ) );
        }
        Decompiler.decompileType( type ).whenCompleteAsync( ( ast, err ) -> {
    		if( err != null ) {
    			if( LOG.isErrorEnabled( ) ) {
    				LOG.error( "Error decompiling type '{}' from container '{}'.", type.getTypeMetadata( ).getFullName( ), type.getOwningContainer( ).getName( ), err );
    			}
                ErrorDialog.displayError( "Error loading AST.", "Error loading AST for type: " + type.getTypeMetadata( ).getFullName( ), err );
            }
            else {
            	if( LOG.isInfoEnabled( ) ) {
            		LOG.info( "Received type AST for type '{}' in container '{}'.", type.getTypeMetadata( ).getFullName( ), type.getOwningContainer( ).getName( ) );
            	}
                buildViewForAST( ast );
            }
    	}, PlatformExecutor.INSTANCE );
    }
    
    public TypeContainerView getContainerView( ) {
        return this.containerView;
    }
    
    public Type getType( ) {
        return this.type;
    }
    
    private void buildViewForAST( CompilationUnit ast ) {
    	Document document = this.contentView.getEngine( ).getDocument( );
    	Element rootElement = document.getDocumentElement( );
    	Node body = rootElement.getElementsByTagName( "BODY" ).item( 0 );
    	
    	NodeList children = body.getChildNodes( );
    	for( int i = 0; i < children.getLength( ); i++ ) {
    		body.removeChild( children.item( i ) );
    	}
    	
    	this.contentView.getEngine( ).loadContent( ast.getText( ), "text/plain" );
    }
    
}
