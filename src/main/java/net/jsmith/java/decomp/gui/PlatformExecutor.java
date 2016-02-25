package net.jsmith.java.decomp.gui;

import java.util.concurrent.Executor;

import javafx.application.Platform;

public class PlatformExecutor implements Executor {

    public static final PlatformExecutor INSTANCE = new PlatformExecutor( );
    
    private PlatformExecutor( ) { }
    
    @Override
    public void execute( Runnable command ) {
        Platform.runLater( command );
    }

}
