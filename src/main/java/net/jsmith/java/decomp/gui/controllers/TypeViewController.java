package net.jsmith.java.decomp.gui.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.jsmith.java.decomp.decompiler.DecompilerUtils;
import net.jsmith.java.decomp.gui.ContainerView;
import net.jsmith.java.decomp.gui.ErrorDialog;
import net.jsmith.java.decomp.gui.WorkspaceView;
import net.jsmith.java.decomp.utils.ThreadPools;
import net.jsmith.java.decomp.workspace.FieldReference;
import net.jsmith.java.decomp.workspace.MethodReference;
import net.jsmith.java.decomp.workspace.Reference;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.TypeReference;
import net.jsmith.java.decomp.workspace.Workspace;
import netscape.javascript.JSObject;

public class TypeViewController implements Controller {
	
	private static final Logger LOG = LoggerFactory.getLogger( TypeViewController.class );

	public static Tab createView( ContainerView containerView, Type type ) {
		TypeViewController controller = new TypeViewController( containerView, type );
		
		Tab tab = new Tab( type.getMetadata( ).getTypeName( ) );
		tab.setContent( FXMLUtils.loadView( "TypeView.fxml", controller ) );
		
		return tab;
	}
	
	public static TypeViewController getController( Tab tab ) {
		return FXMLUtils.getController( tab.getContent( ) );
	}
	
	private final ContainerView containerView;
	private final Type type;
	
	private boolean isDecompiled;
	private Runnable onDecompiled;
	
	@FXML
	private WebView contentView;
	
	private TypeViewController( ContainerView containerView, Type type ) {
		this.containerView = Objects.requireNonNull( containerView, "containerView" );
		this.type = Objects.requireNonNull( type, "type" );
		
		this.isDecompiled = false;
		this.onDecompiled = null;
	}
	
	public Type getType( ) {
		return this.type;
	}
	
	public void addOnDecompile( Runnable r ) {
		if( this.isDecompiled ) {
			r.run( );
		}
		else if( this.onDecompiled == null ) {
			this.onDecompiled = r;
		}
		else {
			Runnable prev = this.onDecompiled;
			this.onDecompiled = ( ) -> {
				prev.run( );
				r.run( );
			};
		}
	}
	
	public void seekToReference( Reference reference ) {
		this.addOnDecompile( ( ) -> {
			String anchorID = reference.toAnchorID( );
	    	Node element = this.contentView.getEngine( ).getDocument( ).getElementById( anchorID );
	    	if( element == null ) {
	    		return;
	    	}
	    	( ( JSObject ) element ).call( "scrollIntoView", true );
		} );
	}
	
	@FXML
	private void initialize( ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Initializing TypeViewController for type '{}' of container '{}'.", this.type.getMetadata( ).getFullName( ), this.type.getContainer( ).getName( ) );
		}
		
		// Disable drag and drop for WebView
		this.contentView.setOnDragEntered( null );
        this.contentView.setOnDragExited( null );
        this.contentView.setOnDragOver( null );
        this.contentView.setOnDragDropped( null );
        this.contentView.setOnDragDetected( null );
        this.contentView.setOnDragDone( null );
        
        WebEngine engine = this.contentView.getEngine( );
        engine.setUserStyleSheetLocation( this.getClass( ).getResource( "/css/type.css" ).toExternalForm( ) );
        
        String loadingMessage = String.format( "Decompiling '%s'...", this.type.getMetadata( ).getFullName( ) );
        engine.loadContent( loadingMessage, "text/plain" );
        
        if( LOG.isInfoEnabled( ) ) {
        	LOG.info( "Decompiling type '{}' of container '{}'.", this.type.getMetadata( ).getFullName( ), this.type.getContainer( ).getName( ) );
        }
        DecompilerUtils.defaultDecompile( this.type ).whenCompleteAsync( ( html, err ) -> {
        	if( err != null ) {
        		if( LOG.isErrorEnabled( ) ) {
        			LOG.error( "Error decompiling '{}' of container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ), err );
        		}
        		ErrorDialog.displayError( "Error decompiling type", "Error decompiling type: " + type.getMetadata( ).getFullName( ), err );
        	}
        	else {
        		if( LOG.isInfoEnabled( ) ) {
        			LOG.info( "Received type html for '{}' of container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
        		}
        		engine.documentProperty( ).addListener( ( obs, oldVal, newVal ) -> {
        			if( newVal != null ) {
        				this.registerEventHandlers( newVal );
        			}
        		} );
        		engine.loadContent( html );
        	}
        }, ThreadPools.PLATFORM );
	}
	
    private void registerEventHandlers( Document document ) {
    	NodeList spans = document.getElementsByTagName( "span" );
    	for( int i = 0; i < spans.getLength( ); i++ ) {
    		Node span = spans.item( i );
    		
    		NamedNodeMap attribs = span.getAttributes( );
    		Node refTypeNode = attribs.getNamedItem( "ref_type" );
    		if( refTypeNode == null ) continue;
    		
    		Reference reference = this.createReferenceFromNode( attribs );
    		( ( EventTarget ) span ).addEventListener( "click", ( evt ) -> {
    			this.handleReferenceClick( reference );
    		}, true );
    	}
    	this.isDecompiled = true;
    	if( this.onDecompiled != null ) {
    		this.onDecompiled.run( );
    		this.onDecompiled = null;
    	}
    }
    
    private Reference createReferenceFromNode( NamedNodeMap attribs ) {
    	String refType = attribs.getNamedItem( "ref_type" ).getTextContent( );
    	if( refType.equals( "type" ) ) {
    		String typeName = attribs.getNamedItem( "type" ).getTextContent( );
    		return new TypeReference( typeName );
    	}
    	else if( refType.equals( "method" ) ) {
    		String typeName = attribs.getNamedItem( "type" ).getTextContent( );
    		String methodName = attribs.getNamedItem( "method_name" ).getTextContent( );
    		String methodSig  = attribs.getNamedItem( "method_sig" ).getTextContent( );
    		
    		return new MethodReference( typeName, methodName, methodSig );
    	}
    	else if( refType.equals( "field" ) ) {
    		String typeName = attribs.getNamedItem( "type" ).getTextContent( );
    		String fieldName = attribs.getNamedItem( "field_name" ).getTextContent( );
    		String fieldType = attribs.getNamedItem( "field_type" ).getTextContent( );
    		
    		return new FieldReference( typeName, fieldName, fieldType );
    	}
    	else {
    		throw new IllegalArgumentException( "Unknown reference type encountered: " + refType );
    	}
    }
    
    private void handleReferenceClick( Reference reference ) {
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Handling reference click for id '{}'.", reference.toAnchorID( ) );
    	}
    	
    	WorkspaceView workspaceView = this.containerView.getWorkspaceView( );
    	Workspace workspace = workspaceView.getWorkspace( );
    	workspace.resolveReference( reference ).whenCompleteAsync( ( types, err ) -> {
    		if( err != null ) {
    			if( LOG.isErrorEnabled( ) ) {
    				LOG.error( "Error resolving reference '{}'.", reference.toAnchorID( ), err );
    			}
    			ErrorDialog.displayError( "Error resolving reference.", "Error resolving reference: " + reference.toAnchorID( ), err );
    		}
    		else {
    			if( LOG.isInfoEnabled( ) ) {
    				LOG.info( "Resolved '{}' instances for reference '{}'.", types.size( ), reference.toAnchorID( ) );
    			}
    			if( types.size( ) >= 1 ) {
    				// TODO: Handle multiple resolutions.
    				workspaceView.openAndShowType( types.get( 0 ), reference );
    			}
    		}
    	}, ThreadPools.PLATFORM );
    }
    
}