package net.jsmith.java.byteforge.workspace.events;

import net.jsmith.java.byteforge.workspace.Container;

import java.util.Objects;

public abstract class ContainerEvent extends WorkspaceEvent {

    private final Container container;

    public ContainerEvent( Container container ) {
        super( Objects.requireNonNull( container, "container" ).getWorkspace( ) );

        this.container = container;
    }

    public Container getContainer( ) {
        return this.container;
    }
}
