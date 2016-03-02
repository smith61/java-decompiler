package net.jsmith.java.decomp.decompiler.procyon;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.languages.java.JavaLanguage;

import net.jsmith.java.decomp.decompiler.Decompiler;
import net.jsmith.java.decomp.document.Document;
import net.jsmith.java.decomp.utils.ThreadPools;
import net.jsmith.java.decomp.workspace.Container;
import net.jsmith.java.decomp.workspace.Type;

public class ProcyonDecompiler implements Decompiler {
	
	private static final Logger LOG = LoggerFactory.getLogger( ProcyonDecompiler.class );
	
	private final Map< Container, ContainerTypeLoader > typeLoaders = new WeakHashMap< >( );
	
	@Override
	public String getName( ) {
		return "Procyon";
	}

	@Override
	public CompletableFuture< String > decompile( Type type ) {
		return ThreadPools.supplyBackground( ( ) -> {
			if( LOG.isInfoEnabled( ) ) {
				LOG.info( "Decompiling type '{}' with procyon decompiler.", type.getMetadata( ).getFullName( ) );
			}
			ContainerTypeLoader loader;
			synchronized( this.typeLoaders ) {
				loader = this.typeLoaders.get( type.getContainer( ) );
				if( loader == null ) {
					loader = new ContainerTypeLoader( type.getContainer( ) );
					this.typeLoaders.put( type.getContainer( ), loader );
				}
			}
			MetadataSystem metadataSystem = loader.getMetadataSystem( );
			TypeDefinition def = metadataSystem.lookupType( type.getMetadata( ).getFullName( ) ).resolve( );
		
			DecompilationOptions options = new DecompilationOptions( );
			options.setFullDecompilation( true );
			options.getSettings( ).setForceExplicitImports( true );
			
			JavaHtmlOutput output = new JavaHtmlOutput( );
			new JavaLanguage( ).decompileType( def, output, options );
			
			return output.getHtml( );
		} );
	}
	
	@Override
	public CompletableFuture< Document > decompileRichText( Type type ) {
		return ThreadPools.supplyBackground( ( ) -> {
			if( LOG.isInfoEnabled( ) ) {
				LOG.info( "Decompiling type '{}' with procyon decompiler.", type.getMetadata( ).getFullName( ) );
			}
			ContainerTypeLoader loader;
			synchronized( this.typeLoaders ) {
				loader = this.typeLoaders.get( type.getContainer( ) );
				if( loader == null ) {
					loader = new ContainerTypeLoader( type.getContainer( ) );
					this.typeLoaders.put( type.getContainer( ), loader );
				}
			}
			MetadataSystem metadataSystem = loader.getMetadataSystem( );
			TypeDefinition def = metadataSystem.lookupType( type.getMetadata( ).getFullName( ) ).resolve( );
		
			DecompilationOptions options = new DecompilationOptions( );
			options.setFullDecompilation( true );
			options.getSettings( ).setForceExplicitImports( true );
			
			JavaRichTextOutput output = new JavaRichTextOutput( );
			new JavaLanguage( ).decompileType( def, output, options );
			
			return output.createDocument( );
		} );
	}

}
