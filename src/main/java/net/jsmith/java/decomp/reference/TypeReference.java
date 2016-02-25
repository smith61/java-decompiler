package net.jsmith.java.decomp.reference;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.strobel.decompiler.languages.java.ast.CompilationUnit;

public class TypeReference {
    
    private final Object LOCK = new Object( );
    
    private final TypeContainer container;
    
    private final String typeName;
    private SoftReference< CompilationUnit > typeAST;
    
    public TypeReference( TypeContainer container, String typeName ) {
        this.container = Objects.requireNonNull( container, "container" );
        
        this.typeName = Objects.requireNonNull( typeName, "typeName" );
        this.typeAST = new SoftReference< >( null );
    }
    
    public TypeContainer getContainer( ) {
        return this.container;
    }
    
    public String getFullyQualifiedName( ) {
        return this.typeName;
    }
    
    public String getPackageName( ) {
        int endIndex = this.typeName.lastIndexOf( '.' );
        if( endIndex <= 0 ) return "";
        
        return this.typeName.substring( 0, endIndex );
    }
    
    public String getClassName( ) {
        int startIndex = this.typeName.lastIndexOf( '.' );
        if( startIndex <= 0 ) return this.typeName;
        
        return this.typeName.substring( startIndex + 1 );
    }
    
    public CompletableFuture< Optional< CompilationUnit > > getTypeAST( ) {
        return CompletableFuture.supplyAsync( ( ) -> {
            CompilationUnit ast = typeAST.get( );
            if( ast == null ) {
                synchronized( LOCK ) {
                    ast = typeAST.get( );
                    if( ast == null ) {
                        ast = getContainer( ).loadASTForType( TypeReference.this );
                        typeAST = new SoftReference< >( ast );
                    }
                }
            }
            return Optional.ofNullable( ast );
        } );
    }
    
}
