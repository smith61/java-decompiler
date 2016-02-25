package net.jsmith.java.decomp.reference;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.languages.java.JavaLanguage;
import com.strobel.decompiler.languages.java.ast.CompilationUnit;

public class NIOPathTypeContainer implements TypeContainer, ITypeLoader {
    
    private final String name;
    
    private final FileSystem fileSystem;
    private final Path rootPath;
    
    private final Map< String, TypeReference > containingTypes;
    
    public NIOPathTypeContainer( String name, FileSystem fileSystem, Path rootPath ) throws IOException {
        this.name = Objects.requireNonNull( name, "name" );
        
        this.fileSystem = Objects.requireNonNull( fileSystem, "fileSystem" );
        this.rootPath = Objects.requireNonNull( rootPath, "rootPath" );
        
        this.containingTypes = Collections.unmodifiableMap( this.loadTypeReferences( rootPath ) );
    }
    
    @Override
    public String getName( ) {
        return this.name;
    }
    
    @Override
    public Set< TypeReference > getContainedTypes( ) {
        return this.containingTypes.values( ).stream( ).collect( Collectors.toSet( ) );
    }
    
    @Override
    public List< TypeReference > resolveTypeReference( String typeName ) {
        if( this.containingTypes.containsKey( typeName ) ) {
            return Arrays.asList( this.containingTypes.get( typeName ) );
        }
        return Collections.emptyList( );
    }
    
    @Override
    public CompilationUnit loadASTForType( TypeReference reference ) {
        if( reference.getContainer( ) != this ) {
            throw new IllegalArgumentException( "Can not load AST for reference not loaded by this container." );
        }
        
        DecompilationOptions options = new DecompilationOptions( );
        options.getSettings( ).setTypeLoader( this );
        options.getSettings( ).setForceExplicitImports( true );
        
        MetadataSystem metadataSystem = new MetadataSystem( this );
        TypeDefinition type = metadataSystem.lookupType( reference.getFullyQualifiedName( ) ).resolve( );
        
        JavaLanguage lang = new JavaLanguage( );
        return lang.decompileTypeToAst( type, options );
    }
    
    @Override
    public void close( ) throws IOException {
        this.fileSystem.close( );
    }

    @Override
    public boolean tryLoadType( String internalName, Buffer buffer ) {
        if( this.containingTypes.containsKey( internalName ) ) {
            try {
                Path loc = this.rootPath.resolve( internalName.replace( '.', '/' ) + ".class" );
                if( !Files.exists( loc ) ) {
                    return false;
                }
                
                byte[ ] typeBytes = Files.readAllBytes( loc );
                buffer.reset( typeBytes.length );
                System.arraycopy( typeBytes, 0, buffer.array( ), 0, typeBytes.length );
                
                return true;
            }
            catch( IOException ioe ) {
                // TODO: Log error;
                return false;
            }
        }
        return false;
    }
    
    private final Map< String, TypeReference > loadTypeReferences( Path rootPath ) throws IOException {
        try( Stream< Path > stream = Files.walk( rootPath ) ) {
            return stream.filter( ( path ) -> {
                return Files.isRegularFile( path ) && path.getFileName( ).toString( ).endsWith( ".class" );
            } ).map( ( path ) -> {
                String typeName = rootPath.relativize( path ).toString( );
                typeName = typeName.substring( 0, typeName.lastIndexOf( '.' ) );
                typeName = typeName.replace( '/', '.' ).replace( '\\', '.' );
                return typeName;
            } ).collect( Collectors.toMap( Function.identity( ), ( typeName ) -> {
                return new TypeReference( NIOPathTypeContainer.this, typeName );
            } ) );
        }
    }
    
}
