package net.jsmith.java.byteforge.workspace.impl;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger( MetadataLoader.class );

	public static MetadataImpl loadFromStream( InputStream is ) throws IOException {
		MetadataVisitor mv = new MetadataVisitor( );
		new ClassReader( is ).accept( mv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES );
		
		return mv.getTypeMetadata( );
	}
	
	private static class MetadataVisitor extends ClassVisitor implements Opcodes {
		
		private final MetadataImpl.Builder builder;
		
		public MetadataVisitor( ) {
			this( null );
		}
		
		public MetadataVisitor( ClassVisitor delegate ) {
			super( ASM5, delegate );
			
			this.builder = MetadataImpl.builder( );
		}

		@Override
		public void visit( int version, int access, String name, String signature, String superName, String[ ] interfaces ) {
			super.visit( version, access, name, signature, superName, interfaces );
			
			if( LOG.isDebugEnabled( ) ) {
				LOG.debug( "Parsing metadata for class '{}' with access flags '{}'.", name, access );
			}
			this.builder.setFullName( name );
			this.builder.setModifiers( access );
		}

		@Override
		public void visitOuterClass( String owner, String name, String desc ) {
			super.visitOuterClass( owner, name, desc );
			
			if( LOG.isDebugEnabled( ) ) {
				LOG.debug( "Found outer class information '{}.{}.{}'", owner, name, desc );
			}
			this.builder.setEnclosingType( owner );
			this.builder.setEnclosingMethod( name, desc );
		}

		@Override
		public void visitInnerClass( String name, String outerName, String innerName, int access ) {
			super.visitInnerClass( name, outerName, innerName, access );
			
			if( LOG.isDebugEnabled( ) ) {
				LOG.debug( "Found inner class reference '{}'.", name );
			}
			this.builder.addEnclosedType( name );
		}

		public MetadataImpl getTypeMetadata( ) {
			return this.builder.build( );
		}
		
	}
	
}
