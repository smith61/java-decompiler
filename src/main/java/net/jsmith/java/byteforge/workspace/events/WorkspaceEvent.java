package net.jsmith.java.byteforge.workspace.events;

import net.jsmith.java.byteforge.workspace.Workspace;

import java.util.Objects;

public abstract class WorkspaceEvent {

    private final Workspace workspace;

    protected WorkspaceEvent( Workspace workspace ) {
        this.workspace = Objects.requireNonNull( workspace, "workspace" );
    }

    public Workspace getWorkspace( ) {
        return this.workspace;
    }
}
