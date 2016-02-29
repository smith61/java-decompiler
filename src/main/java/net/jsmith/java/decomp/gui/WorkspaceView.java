package net.jsmith.java.decomp.gui;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.Workspace;

public class WorkspaceView extends ScrollPane {

	private static final Logger LOG = LoggerFactory.getLogger( WorkspaceView.class );
	
	private final Workspace workspace;
	
    private final TabPane containersTab;
    
    public WorkspaceView( Workspace workspace ) {
    	this.workspace = Objects.requireNonNull( workspace, "workspace" );
    	
        this.containersTab = new TabPane( );
        this.setContent( this.containersTab );
        
        this.setOnDragOver( ( evt ) -> {
            Dragboard db = evt.getDragboard( );
            if( db.hasFiles( ) ) {
                evt.consume( );
                
                evt.acceptTransferModes( TransferMode.ANY );
            }
        } );
        this.setOnDragDropped( ( evt ) -> {
            Dragboard db = evt.getDragboard( );
            if( db.hasFiles( ) ) {
                evt.consume( );
            	List< File > files = db.getFiles( );
            	if( LOG.isDebugEnabled( ) ) {
            		LOG.debug( "File drop event captured for files '{}'.", files );
            	}
                
                files.stream( ).forEach( this::openAndShowFile );
            }
        } );
        this.containersTab.getTabs( ).addListener( ( Change< ? extends Tab > c ) -> {
        	while( c.next( ) ) {
        		if( c.wasRemoved( ) ) {
        			for( Tab removed : c.getRemoved( ) ) {
        				ContainerView cv = ( ContainerView ) removed.getContent( );
        				cv.getContainer( ).close( );
        			}
        		}
        	}
        } );
        
        workspace.setErrorListener( ListenerUtils.onFXThread( ( err ) -> {
        	ErrorDialog.displayError( "Error in workspace", "Error in workspace threads.", err );
        } ) );
        workspace.setContainerClosedListener( ListenerUtils.onFXThread( ( container ) -> {
        	if( LOG.isInfoEnabled( ) ) {
        		LOG.info( "Received container closed event for container '{}'.", container.getName( ) );
        	}
        	this.containersTab.getTabs( ).stream( ).filter( ( tab ) -> {
        		return ( ( ContainerView ) tab.getContent( ) ).getContainer( ) == container;
        	} ).findFirst( ).ifPresent( ( tab ) -> {
        		this.containersTab.getTabs( ).remove( tab );
        	} );
        } ) );
        workspace.setContainerOpenedListener( ListenerUtils.onFXThread( ( container ) -> {
        	if( LOG.isInfoEnabled( ) ) {
        		LOG.info( "Received container opened event for container '{}'.", container.getName( ) );
        	}
        	Tab tab = new Tab( );
        	tab.setText( container.getName( ) );
        	tab.setContent( new ContainerView( this, container ) );
        	
        	this.containersTab.getTabs( ).add( tab );
        	this.containersTab.getSelectionModel( ).select( tab );
        } ) );
    }
    
    public Workspace getWorkspace( ) {
    	return this.workspace;
    }
    
    public void openAndShowType( Type type ) {
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Opening and showing type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ) );
    	}
        Tab tab = this.containersTab.getTabs( ).stream( ).filter( ( t ) -> {
            ContainerView view = ( ContainerView ) t.getContent( );
            return view.getContainer( ) == type.getContainer( );
        } ).findFirst( ).orElseThrow( ( ) -> {
        	if( LOG.isWarnEnabled( ) ) {
        		LOG.warn( "Could not find container window for container '{}'.", type.getContainer( ).getName( ) );
        	}
            return new IllegalArgumentException( "Unable to locate container view for container: " + type.getContainer( ).getName( ) );
        } );
        
        this.containersTab.getSelectionModel( ).select( tab );
        ( ( ContainerView ) tab.getContent( ) ).openAndShowType( type );
    }
    
    public void openAndShowFile( File file ) {
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Opening and showing file '{}'.", file );
    	}
    	this.getWorkspace( ).openContainerAtPath( Paths.get( file.toURI( ) ) );
    }
    
}
