package net.jsmith.java.decomp.html;

import java.util.concurrent.CompletableFuture;

import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.languages.java.JavaLanguage;

import net.jsmith.java.decomp.container.Type;

public class HtmlGenerator {

	public static CompletableFuture< String > renderToHtml( Type type ) {
		return CompletableFuture.supplyAsync( ( ) -> {
			TypeDefinition def = type.getTypeDefinition( );
			
			JavaHtmlOutput output = new JavaHtmlOutput( );
			
			DecompilationOptions options = new DecompilationOptions( );
			options.setFullDecompilation( true );
			
			new JavaLanguage( ).decompileType( def, output, options );
			return output.getHtml( );
		} );
	}
	
}
