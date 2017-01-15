package net.jsmith.java.byteforge.workspace;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WorkspaceIndex {

    int RESOLVE_EXACT = 1 << 0;

    CompletableFuture< List< Type > > resolveReference( Reference reference, int resolveFlags );

}
