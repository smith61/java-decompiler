package net.jsmith.java.byteforge.workspace.events;

import net.jsmith.java.byteforge.workspace.Workspace;

import java.util.Objects;

public class WorkspaceErrorEvent extends WorkspaceEvent {

    private final Throwable error;

    public WorkspaceErrorEvent( Workspace workspace, Throwable error ) {
        super( workspace );

        this.error = Objects.requireNonNull( error, "error" );
    }

    public Throwable getError( ) {
        return this.error;
    }
}
