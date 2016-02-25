package net.jsmith.java.decomp.reference;

import java.io.Closeable;
import java.util.Set;

import com.strobel.decompiler.languages.java.ast.CompilationUnit;

public interface TypeContainer extends ReferenceResolver, Closeable {

    Set< TypeReference > getContainedTypes( );
    
    CompilationUnit loadASTForType( TypeReference reference );
    
}
