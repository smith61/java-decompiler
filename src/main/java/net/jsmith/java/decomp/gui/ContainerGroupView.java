package net.jsmith.java.decomp.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import net.jsmith.java.decomp.container.GroupReferenceResolver;
import net.jsmith.java.decomp.container.ReferenceResolver;
import net.jsmith.java.decomp.container.Type;
import net.jsmith.java.decomp.container.TypeContainer;
import net.jsmith.java.decomp.container.TypeContainerUtils;

public class ContainerGroupView extends ScrollPane {

	private static final Logger LOG = LoggerFactory.getLogger( ContainerGroupView.class );
	
    private final TabPane containersTab;
    
    private final GroupReferenceResolver referenceResolver;
    
    public ContainerGroupView( ) {
        this.containersTab = new TabPane( );
        this.setContent( this.containersTab );
        
        this.referenceResolver = new GroupReferenceResolver( );
        
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
                        TypeContainer typeContainer = ( ( TypeContainerView ) removed.getContent( ) ).getTypeContainer( );
                        if( LOG.isInfoEnabled( ) ) {
                        	LOG.info( "Detected TypeContainerView close for container '{}'.", typeContainer.getName( ) );
                        }
                        this.referenceResolver.removeResolver( typeContainer );
                        try {
                            typeContainer.close( );
                        }
                        catch( IOException ioe ) {
                        	if( LOG.isErrorEnabled( ) ) {
                        		LOG.error( "Error closing container '{}'.", typeContainer.getName( ), ioe );
                        	}
                            ErrorDialog.displayError( "Error closing TypeContainer", "Error closing TypeContainer: " + typeContainer.getName( ), ioe );
                        }
                    }
                }
            }
        } );
    }
    
    public ReferenceResolver getReferenceResolver( ) {
        return this.referenceResolver;
    }
    
    public void openAndShowType( Type type ) {
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Opening and showing type '{}' in container '{}'.", type.getTypeMetadata( ).getFullName( ), type.getOwningContainer( ).getName( ) );
    	}
        Tab tab = this.containersTab.getTabs( ).stream( ).filter( ( t ) -> {
            TypeContainerView view = ( TypeContainerView ) t.getContent( );
            return view.getTypeContainer( ) == type.getOwningContainer( );
        } ).findFirst( ).orElseThrow( ( ) -> {
        	if( LOG.isWarnEnabled( ) ) {
        		LOG.warn( "Could not find container window for container '{}'.", type.getOwningContainer( ).getName( ) );
        	}
            return new IllegalArgumentException( "Unable to locate container view for container: " + type.getOwningContainer( ).getName( ) );
        } );
        
        this.containersTab.getSelectionModel( ).select( tab );
        ( ( TypeContainerView ) tab.getContent( ) ).openAndShowType( type );
    }
    
    public void openAndShowFile( File file ) {
    	if( LOG.isInfoEnabled( ) ) {
    		LOG.info( "Opening and showing file '{}'.", file );
    	}
    	TypeContainerUtils.createTypeContainerFromJar( Paths.get( file.toURI( ) ) ).whenCompleteAsync( ( container, err ) -> {
    		if( err != null ) {
    			if( LOG.isErrorEnabled( ) ) {
    				LOG.error( "Error loading container from file '{}'.", file, err );
    			}
    			ErrorDialog.displayError( "Error loading type container", "Error loading type container from file: " + file, err );
    		}
    		else {
    			Optional< Tab > tab = containersTab.getTabs( ).stream( ).filter( ( t ) -> {
    				TypeContainerView view = ( TypeContainerView ) t.getContent( );
    				return view.getTypeContainer( ).getName( ).equals( container.getName( ) );
    			} ).findFirst( );
    			if( tab.isPresent( ) ) {
    				if( LOG.isWarnEnabled( ) ) {
    					LOG.warn( "Type container already loaded with name '{}'.", container.getName( ) );
    				}
    				try {
    					container.close( );
    				}
    				catch( IOException ioe ) { }
    				containersTab.getSelectionModel( ).select( tab.get( ) );
    			}
    			else {
    				this.referenceResolver.addResolver( container );
    				
    				Tab t = new Tab( );
    				t.setText( container.getName( ) );
    				t.setContent( new TypeContainerView( this, container ) );
    				
    				containersTab.getTabs( ).add( t );
    				containersTab.getSelectionModel( ).select( t );
    			}
    		}
    	}, PlatformExecutor.INSTANCE );
    }
    
}
