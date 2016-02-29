package net.jsmith.java.decomp.gui;

import java.io.File;
import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ApplicationMenuBar extends MenuBar {

    private final WorkspaceView containerGroup;
    
    public ApplicationMenuBar( WorkspaceView containerGroup ) {
        this.containerGroup = Objects.requireNonNull( containerGroup, "containerGroup" );
        
        this.getMenus( ).add( this.buildFileMenu( ) );
    }
    
    private Menu buildFileMenu( ) {
        Menu fileMenu = new Menu( "File" );
        
        MenuItem open = new MenuItem( "Open" );
        open.setOnAction( ( evt ) -> {
            FileChooser fChooser = new FileChooser( );
            fChooser.getExtensionFilters( ).add( new ExtensionFilter( "Jar File", "*.jar" ) );
            
            List< File > selected = fChooser.showOpenMultipleDialog( getScene( ).getWindow( ) );
            if( selected != null ) {
                selected.stream( ).forEach( ( file ) -> {
                    containerGroup.openAndShowFile( file );
                } );
            }
        } );
        
        MenuItem exit = new MenuItem( "Exit" );
        exit.setOnAction( ( evt ) -> {
            Platform.exit( );
        } );
        
        fileMenu.getItems( ).addAll( open, new SeparatorMenuItem( ), exit );
        
        return fileMenu;
    }
    
}
