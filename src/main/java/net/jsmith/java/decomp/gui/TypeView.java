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
import net.jsmith.java.decomp.utils.ThreadPools;
import net.jsmith.java.decomp.workspace.FieldReference;
import net.jsmith.java.decomp.workspace.MethodReference;
import net.jsmith.java.decomp.workspace.Reference;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.TypeReference;
import net.jsmith.java.decomp.workspace.Workspace;
import netscape.javascript.JSObject;

public class TypeView extends BorderPane {

	private static final Logger LOG = LoggerFactory.getLogger( TypeView.class );
	private static final String TYPE_STYLESHEET = TypeView.class.getResource( "/css/type.css" ).toString( );
	
    private final ContainerView containerView;
    private final Type type;
    
    private final WebView contentView;
    
    private boolean isDecompiled;
    private Runnable onDecompiled;
    
    public TypeView( ContainerView containerView, Type type ) {
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        this.type = Objects.requireNonNull( type, "type" );
        
        this.contentView = new WebView( );
        this.contentView.setOnDragEntered( null );
        this.contentView.setOnDragExited( null );
        this.contentView.setOnDragOver( null );
        this.contentView.setOnDragDropped( null );
        this.contentView.setOnDragDetected( null );
        this.contentView.setOnDragDone( null );
        
        this.setCenter( this.contentView );
        
        this.isDecompiled = false;
        this.onDecompiled = null;
        
        WebEngine engine = this.contentView.getEngine( );
    	engine.setUserStyleSheetLocation( TYPE_STYLESHEET );
        engine.loadContent( "Loading..." );
        
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
                engine.documentProperty( ).addListener( ( obs, oldVal, newVal ) -> {
                	if( newVal != null ) {
                		registerEventHandlers( newVal );
                	}
                } );
            	engine.loadContent( html );
            }
    	}, ThreadPools.PLATFORM );
    }
    
    public ContainerView getContainerView( ) {
        return this.containerView;
    }
    
    public Type getType( ) {
        return this.type;
    }
    
    public void seekToReference( Reference reference ) {
    	this.setOnDecompiled( ( ) -> {
    		this.seekToAnchor( reference.toAnchorID( ) );
    	} );
    }
    
    private void setOnDecompiled( Runnable runnable ) {
    	if( this.isDecompiled ) {
    		runnable.run( );
    	}
    	else if( this.onDecompiled != null ) {
    		Runnable prev = this.onDecompiled;
    		this.onDecompiled = ( ) -> {
    			prev.run( );
    			runnable.run( );
    		};
    	}
    	else {
    		this.onDecompiled = runnable;
    	}
    }
    
    private boolean seekToAnchor( String anchorID ) {
    	Node element = this.contentView.getEngine( ).getDocument( ).getElementById( anchorID );
    	if( element == null ) {
    		return false;
    	}
    	( ( JSObject ) element ).call( "scrollIntoView", true );
    	
    	return true;
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
