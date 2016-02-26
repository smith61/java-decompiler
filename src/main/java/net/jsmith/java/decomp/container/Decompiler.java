package net.jsmith.java.decomp.container;

import java.util.concurrent.CompletableFuture;

import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.languages.java.JavaLanguage;
import com.strobel.decompiler.languages.java.ast.CompilationUnit;

public class Decompiler {

	public static CompletableFuture< CompilationUnit > decompileType( Type type ) {
		return CompletableFuture.supplyAsync( ( ) -> {
			TypeDefinition typeDefinition = type.getTypeDefinition( );
			
			// Load in all method bodies
			typeDefinition.getDeclaredMethods( ).stream( ).forEach( MethodDefinition::getBody );
			
			DecompilationOptions options = new DecompilationOptions( );
			options.getSettings( ).setForceExplicitImports( true );
			options.setFullDecompilation( true );
			
			return new JavaLanguage( ).decompileTypeToAst( typeDefinition, options );
		} );
	}
	
}
