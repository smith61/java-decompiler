package net.jsmith.java.byteforge.workspace.events;

import net.jsmith.java.byteforge.workspace.Type;

import java.util.Objects;

public class TypeLoadEvent extends ContainerEvent {

    private final Type type;

    public TypeLoadEvent( Type type ) {
        super( Objects.requireNonNull( type, "type" ).getContainer( ) );

        this.type = type;
    }

    public Type getType( ) {
        return this.type;
    }
}
