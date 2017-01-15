package net.jsmith.java.byteforge.workspace.impl;

import net.jsmith.java.byteforge.workspace.WorkspaceIndex;
import net.jsmith.java.byteforge.workspace.Reference;
import net.jsmith.java.byteforge.workspace.Type;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class WorkspaceIndexImpl implements WorkspaceIndex {

    private final WorkspaceImpl workspace;

    public WorkspaceIndexImpl(WorkspaceImpl workspace ) {
        this.workspace = Objects.requireNonNull( workspace, "workspace" );
    }

    @Override
    public CompletableFuture< List< Type > > resolveReference( Reference reference, int resolveFlags ) {
        if( resolveFlags != WorkspaceIndex.RESOLVE_EXACT ) {
            throw new IllegalArgumentException( "Invalid resolve flags: " + resolveFlags );
        }

        return this.workspace.withReferenceAsync( ( ) -> {
            return new ReferenceResolver( this.workspace, reference ).resolve( );
        } );
    }


}
