package net.jsmith.java.byteforge.workspace.events;

import net.jsmith.java.byteforge.workspace.Container;

public class ContainerOpenedEvent extends ContainerEvent {

    public ContainerOpenedEvent( Container container ) {
        super( container );
    }
}
