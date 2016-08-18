package net.jsmith.java.byteforge.gui.controllers;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import com.google.common.eventbus.Subscribe;
import net.jsmith.java.byteforge.workspace.events.ContainerClosedEvent;
import net.jsmith.java.byteforge.workspace.events.ContainerOpenedEvent;
import net.jsmith.java.byteforge.workspace.events.WorkspaceErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jsmith.java.byteforge.gui.ErrorDialog;
import net.jsmith.java.byteforge.workspace.Container;
import net.jsmith.java.byteforge.workspace.Reference;
import net.jsmith.java.byteforge.workspace.Type;
import net.jsmith.java.byteforge.workspace.Workspace;

public class WorkspaceViewController implements Controller {
	
	private static final Logger LOG = LoggerFactory.getLogger( WorkspaceViewController.class );
	
	public static Node createView( Workspace workspace ) {
		WorkspaceViewController controller = new WorkspaceViewController( workspace );
		
		return FXMLUtils.loadView( "WorkspaceView.fxml", controller );
	}
	
	public static WorkspaceViewController getController( Node node ) {
		return FXMLUtils.getController( node );
	}
	
	private final Workspace workspace;
	
	@FXML
	private TabPane containerTabs;
	
	private WorkspaceViewController( Workspace workspace ) {
		this.workspace = Objects.requireNonNull( workspace, "workspace" );
	}
	
	public Workspace getWorkspace( ) {
		return this.workspace;
	}
	
	public void openAndShowType( Type type ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Opening and showing type '{}' in container '{}'.", type.getMetadata( ).getFullName( ),
					type.getContainer( ).getName( ) );
		}
		Tab tab = this.getTabForContainer( type.getContainer( ) );
		this.containerTabs.getSelectionModel( ).select( tab );
		ContainerViewController.getController( tab ).openAndShowType( type );
	}
	
	public void openAndShowType( Type type, Reference reference ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Opening and showing type '{}' in container '{}' and seeking to reference '{}'.",
					type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ), reference.toAnchorID( ) );
		}
		Tab tab = this.getTabForContainer( type.getContainer( ) );
		this.containerTabs.getSelectionModel( ).select( tab );
		ContainerViewController.getController( tab ).openAndShowType( type, reference );
	}
	
	public void openAndShowFile( File file ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Opening and showing file '{}'.", file );
		}
		this.getWorkspace( ).openContainerAtPath( Paths.get( file.toURI( ) ) );
	}
	
	private void addContainer( Container container ) {
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Received container opened event for container '{}'.", container.getName( ) );
		}
		
		Tab tab = this.containerTabs.getTabs( ).stream( ).filter( ( t ) -> {
			ContainerViewController view = ContainerViewController.getController( t );
			return view.getContainer( ) == container;
		} ).findFirst( ).orElseGet( ( ) -> {
			Tab t = ContainerViewController.createView( this, container );
			
			this.containerTabs.getTabs( ).add( t );
			return t;
		} );
		this.containerTabs.getSelectionModel( ).select( tab );
	}

	private Tab getTabForContainer( Container container ) {
	    return this.containerTabs.getTabs( ).stream( ).filter( ( t ) -> {
	        ContainerViewController view = ContainerViewController.getController( t );
            return view.getContainer( ) == container;
        } ).findFirst( ).orElseThrow( ( ) -> {
            if( LOG.isWarnEnabled( ) ) {
                LOG.warn( "Could not find container window for container '{}'.", container.getName( ) );
            }
            return new IllegalArgumentException(
                    "Unable to locate container view for container: " + container.getName( ) );
        } );
    }

    @Subscribe
    private void onWorkspaceError( WorkspaceErrorEvent event ) {
        ErrorDialog.displayError( "Error in workspace.", "Error in workspace threads.", event.getError( ) );
    }

    @Subscribe
    private void onContainerOpened( ContainerOpenedEvent event ) {
        this.addContainer( event.getContainer( ) );
    }

    @Subscribe
    private void onContainerClosed( ContainerClosedEvent event ) {
        Container container = event.getContainer( );
        if( LOG.isInfoEnabled( ) ) {
            LOG.info( "Received container closed event for container '{}'.", container.getName( ) );
        }
        this.containerTabs.getTabs( ).stream( ).filter( ( tab ) -> {
            return ContainerViewController.getController( tab ).getContainer( ) == container;
        } ).findFirst( ).ifPresent( ( tab ) -> {
            this.containerTabs.getTabs( ).remove( tab );
        } );
    }
	
	@FXML
	private void initialize( ) {
		this.containerTabs.getTabs( ).addListener( ( Change< ? extends Tab > c ) -> {
			while( c.next( ) ) {
				if( c.wasRemoved( ) ) {
					for( Tab removed : c.getRemoved( ) ) {
						ContainerViewController cv = ContainerViewController.getController( removed );
						cv.getContainer( ).close( );
					}
				}
			}
		} );

        workspace.getEventBus( ).register( this );
		workspace.getContainers( ).forEach( this::addContainer );
	}
	
	@FXML
	private void onDragOver( DragEvent evt ) {
		Dragboard db = evt.getDragboard( );
		if( db.hasFiles( ) ) {
			evt.consume( );
			
			evt.acceptTransferModes( TransferMode.ANY );
		}
	}
	
	@FXML
	private void onDragDropped( DragEvent evt ) {
		Dragboard db = evt.getDragboard( );
		if( db.hasFiles( ) ) {
			evt.consume( );
			List< File > files = db.getFiles( );
			if( LOG.isDebugEnabled( ) ) {
				LOG.debug( "File drop event captured for files '{}'.", files );
			}
			
			files.stream( ).forEach( this::openAndShowFile );
		}
	}
	
}
