package net.jsmith.java.byteforge.gui.controllers;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import net.jsmith.java.byteforge.gui.ErrorDialog;
import net.jsmith.java.byteforge.utils.IOUtils;
import net.jsmith.java.byteforge.utils.ThreadPools;
import net.jsmith.java.byteforge.utils.XMLStreamSupport;
import net.jsmith.java.byteforge.workspace.FieldReference;
import net.jsmith.java.byteforge.workspace.MethodReference;
import net.jsmith.java.byteforge.workspace.Reference;
import net.jsmith.java.byteforge.workspace.TypeReference;
import net.jsmith.java.byteforge.workspace.Workspace;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class TypeViewController implements Controller, ChangeListener< Boolean > {
	
	private static final Logger LOG = LoggerFactory.getLogger( TypeViewController.class );
	
	public static javafx.scene.Node createView( ContainerViewController containerView) {
		TypeViewController controller = new TypeViewController( containerView );
		
		return FXMLUtils.loadView( "TypeView.fxml", controller );
	}
	
	private final ContainerViewController containerView;
	
	private TypeViewState currentViewState;
	
	@FXML
	private WebView contentView;
	
	@FXML
	private GridPane searchBar;
	
	@FXML
	private TextField searchText;
	
	private TypeViewController( ContainerViewController containerView) {
		this.containerView = Objects.requireNonNull( containerView, "containerView" );
	}
	
	public void seekToReference( Reference reference ) {
		if( !this.trySeek( reference ) ) {
			this.currentViewState.setSeekReference( reference );
		}
		else {
			this.currentViewState.setSeekReference( null );
		}
	}
	
	public TypeViewState getCurrentViewState( ) {
		return this.currentViewState;
	}
	
	public void setCurrentViewState( TypeViewState nState ) {
		if( this.currentViewState == nState ) {
			return;
		}
		
		if( LOG.isTraceEnabled( ) ) {
			String fromTypeName = "NONE";
			String toTypeName = "NONE";
			if( this.currentViewState != null ) {
				fromTypeName = this.currentViewState.getType( ).getMetadata( ).getFullName( );
			}
			if( nState != null ) {
				toTypeName = nState.getType( ).getMetadata( ).getFullName( );
			}
			
			LOG.trace( "Switching type view from type '{}' to type '{}' in container '{}'.",
					fromTypeName, toTypeName, this.containerView.getContainer( ).getName( ) );
		}
		
		if( this.currentViewState != null ) {
			this.currentViewState.getIsDecompiledProperty( ).removeListener( this );
			if( this.isSearchBarVisible( ) ) {
				this.updateSearch( "" );
			}
			
			Document document = this.contentView.getEngine( ).getDocument( );
			if( document != null ) {
				JSObject asJS = ( JSObject ) document;
				this.currentViewState.setSeekLocation( ( Integer ) asJS.eval( "window.pageYOffset" ) );
			}
			else {
				this.currentViewState.setSeekLocation( 0 );
			}
		}
		
		this.currentViewState = nState;
		
		if( this.currentViewState != null ) {
			this.contentView.getEngine( ).loadContent( this.currentViewState.getDecompiledContent( ) );
			this.currentViewState.getIsDecompiledProperty( ).addListener( this );
		}
	}
	
	public void setSearchBarVisible( boolean visible ) {
		this.searchBar.setVisible( visible );
		this.searchBar.setManaged( visible );
		if( visible ) {
			this.searchText.requestFocus( );
			this.updateSearch( this.searchText.getText( ) );
		}
		else {
			this.updateSearch( "" );
		}
	}
	
	public boolean isSearchBarVisible( ) {
		return this.searchBar.isVisible( );
	}
	
	@FXML
	private void initialize( ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Initializing TypeViewController for container '{}'.", this.containerView.getContainer( ).getName( ) );
		}
		
		// Disable drag and drop for WebView
		this.contentView.setOnDragEntered( null );
		this.contentView.setOnDragExited( null );
		this.contentView.setOnDragOver( null );
		this.contentView.setOnDragDropped( null );
		this.contentView.setOnDragDetected( null );
		this.contentView.setOnDragDone( null );
		this.contentView.getEngine( ).setOnError( this::onJavascriptError );
		this.contentView.getEngine( ).documentProperty( ).addListener( ( obs, pVal, nVal ) -> {
			if( nVal == null ) return;
			
			this.loadScripts( );
			this.loadLineStylesheet( nVal );
			this.registerEventHandlers( nVal );
			
			if( this.isSearchBarVisible( ) ) {
				this.updateSearch( this.searchText.getText( ) );
			}
			
			if( this.currentViewState.getSeekReference( ) != null ) {
				this.seekToReference( this.currentViewState.getSeekReference( ) );
			}
			else {
				JSObject asJS = ( JSObject ) nVal;
				asJS.eval( String.format( "window.scrollTo(0, %d)", this.currentViewState.getSeekLocation( ) ) );
			}
		} );
		
		this.searchBar.setVisible( false );
		this.searchBar.setManaged( false );
		this.searchText.textProperty( ).addListener( ( obs, oldVal, newVal ) -> {
			this.updateSearch( newVal );
		} );
		
		WebEngine engine = this.contentView.getEngine( );
		engine.setUserStyleSheetLocation( this.getClass( ).getResource( "/css/type.css" ).toExternalForm( ) );
	}
	
	@FXML
	private void onKeyPressed( KeyEvent evt ) {
		if( evt.getCode( ) == KeyCode.F ) {
			if( evt.isControlDown( ) ) {
				evt.consume( );
				
				this.setSearchBarVisible( !this.isSearchBarVisible( ) );
			}
		}
	}
	
	@FXML
	private void onSearchKeyPress( KeyEvent evt ) {
		if( evt.getCode( ) == KeyCode.ENTER ) {
			this.nextMatch( );
		}
	}
	
	private void onJavascriptError( WebErrorEvent evt ) {
		if( LOG.isErrorEnabled( ) ) {
			LOG.error( "Error in javascript: '{}'.", evt.getMessage( ), evt.getException( ) );
		}
		ErrorDialog.displayError( "Error in javascript", "Error in javascript: " + evt.getMessage( ),
				evt.getException( ) );
	}
	
	private void registerEventHandlers( Document document ) {
		XMLStreamSupport.stream( document.getElementsByTagName( "span" ) ).filter( ( n ) -> {
			return n.getAttributes( ).getNamedItem( "ref_type" ) != null;
		} ).forEach( ( n ) -> {
			Reference reference = this.createReferenceFromNode( n.getAttributes( ) );
			( ( EventTarget ) n ).addEventListener( "click", ( evt ) -> {
				this.handleReferenceClick( reference );
			}, true );
		} );
	}
	
	private void loadScripts( ) {
		try {
			WebEngine engine = this.contentView.getEngine( );
			engine.executeScript( IOUtils.readResourceAsString( "/js/findAndReplaceDOMText.js" ) );
			engine.executeScript( IOUtils.readResourceAsString( "/js/text_search.js" ) );
		}
		catch( IOException | JSException err ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading scripts into document.", err );
			}
			ErrorDialog.displayError( "Error loading javascript utility scripts",
					"Error loading javascript utility scripts.", err );
		}
	}
	
	private void loadLineStylesheet( Document document ) {
		try {
			long linecount = XMLStreamSupport.stream( document.getElementsByTagName( "span" ) ).filter( ( n ) -> {
				Node style = n.getAttributes( ).getNamedItem( "class" );
				return style != null && style.getTextContent( ).contains( "java_line" );
			} ).count( );
			
			int digits = Long.toString( linecount ).length( );
			
			String css = IOUtils.readResourceAsString( "/css/line_numbers.css.template" );
			css = css.replace( "${NUM_DIGITS}", "" + digits );
			
			Node styleNode = document.createElement( "style" );
			Node textNode = document.createTextNode( css );
			
			styleNode.appendChild( textNode );
			document.getElementsByTagName( "head" ).item( 0 ).appendChild( styleNode );
		}
		catch( IOException ioe ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading stylesheets into WebView.", ioe );
			}
			ErrorDialog.displayError( "Error loading stylesheets", "Error loading stylesheets into WebView", ioe );
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
			String methodSig = attribs.getNamedItem( "method_sig" ).getTextContent( );
			
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
		
		WorkspaceViewController workspaceView = this.containerView.getWorkspaceView( );
		Workspace workspace = workspaceView.getWorkspace( );
		workspace.resolveReference( reference ).whenCompleteAsync( ( types, err ) -> {
			if( err != null ) {
				if( LOG.isErrorEnabled( ) ) {
					LOG.error( "Error resolving reference '{}'.", reference.toAnchorID( ), err );
				}
				ErrorDialog.displayError( "Error resolving reference.",
						"Error resolving reference: " + reference.toAnchorID( ), err );
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
	
	private boolean trySeek( Reference reference ) {
		if( !this.currentViewState.getIsDecompiled( ) ) {
			return false;
		}
		
		Document document = this.contentView.getEngine( ).getDocument( );
		if( document == null ) {
			return false;
		}
		
		Node element = document.getElementById( reference.toAnchorID( ) );
		if( element == null ) {
			return false;
		}
		
		( ( JSObject ) element ).call( "scrollIntoView", true );
		return true;
	}
	
	private void updateSearch( String text ) {
		JSObject root = ( JSObject ) this.contentView.getEngine( ).getDocument( );
		root.call( "update_search", text );
	}
	
	@FXML
	private void nextMatch( ) {
		JSObject root = ( JSObject ) this.contentView.getEngine( ).getDocument( );
		root.call( "next_match" );
	}
	
	@FXML
	private void prevMatch( ) {
		JSObject root = ( JSObject ) this.contentView.getEngine( ).getDocument( );
		root.call( "prev_match" );
	}

	@Override
	public void changed( ObservableValue< ? extends Boolean > observable, Boolean oldValue, Boolean newValue ) {
		this.contentView.getEngine( ).loadContent( this.currentViewState.getDecompiledContent( ) );
	}
	
}
