package net.jsmith.java.decomp.decompiler.procyon;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fxmisc.richtext.StyleSpansBuilder;

import com.strobel.assembler.metadata.FieldDefinition;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.PackageReference;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.assembler.metadata.ParameterReference;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.VariableDefinition;
import com.strobel.assembler.metadata.VariableReference;
import com.strobel.decompiler.ITextOutput;

import net.jsmith.java.decomp.decompiler.CSS;
import net.jsmith.java.decomp.document.Document;

public class JavaRichTextOutput implements ITextOutput {
	
	private static final int INDENT_DEPTH = 4;

	private final StringWriter writer;
	
	private final StyleSpansBuilder< Collection< String > > ssb;
	
	private final Set< VariableReference > seenVariables;
	
	private boolean needsIndent;
	private int indentDepth;
	
	private int lastStyledOffset;
	
	public JavaRichTextOutput( ) {
		this.writer = new StringWriter( );
		
		this.ssb = new StyleSpansBuilder< >( );
		
		this.seenVariables = new HashSet< >( );
		
		this.needsIndent = true;
		this.indentDepth = 0;
		
		this.lastStyledOffset = 0;
	}
	
	public Document createDocument( ) {
		return new Document( this.writer.toString( ), this.ssb.create( ), new ArrayList< >( ), new ArrayList< >( ) );
	}

	@Override
	public void indent( ) {
		this.indentDepth += INDENT_DEPTH;
	}

	@Override
	public void unindent( ) {
		this.indentDepth -= INDENT_DEPTH;
	}

	@Override
	public void write( char ch ) {
		this.write( "" + ch );
	}

	@Override
	public void write( String text ) {
		this.writeIndent( );
		this.writer.write( text );
	}
	
	@Override
	public void write( String format, Object... args ) {
		this.write( String.format( format, args ) );
	}

	@Override
	public void writeLine( String text ) {
		this.write( text );
		this.writeLine( );
	}

	@Override
	public void writeLine( String format, Object... args ) {
		this.writeLine( String.format( format, args ) );
	}

	@Override
	public void writeLine( ) {
		this.writer.write( '\n' );
		this.needsIndent = true;
	}

	@Override
	public void writeDelimiter( String text ) {
		this.write( text );
	}

	@Override
	public void writeOperator( String text ) {
		this.write( text );
	}
	
	@Override
	public void writeAttribute( String text ) {
		this.write( text );
	}
	
	@Override
	public void writeLabel( String value ) {
		this.writeStyled( value, CSS.JAVA_LABEL );
	}
	
	@Override
	public void writeLiteral( Object value ) {
		this.writeStyled( value.toString( ), CSS.JAVA_LITERAL_NUMBER );
	}

	@Override
	public void writeTextLiteral( Object value ) {
		this.writeStyled( value.toString( ), CSS.JAVA_LITERAL_TEXT );
	}

	@Override
	public void writeComment( String value ) {
		this.writeStyled( value, CSS.JAVA_COMMENT );
	}

	@Override
	public void writeComment( String format, Object... args ) {
		this.writeComment( String.format( format, args ) );
	}

	@Override
	public void writeKeyword( String text ) {
		this.writeStyled( text, CSS.JAVA_KEYWORD( text ) );
	}

	@Override
	public void writeDefinition( String text, Object definition ) {
		this.writeDefinition( text, definition, true );
	}

	@Override
	public void writeDefinition( String text, Object definition, boolean isLocal ) {
		if( definition instanceof TypeDefinition ) {
			TypeDefinition def = ( TypeDefinition ) definition;
			
			String id = String.format( "type:%s", def.getInternalName( ) );
			this.writeStyled( text, CSS.JAVA_DEF_TYPE );
		}
		else if( definition instanceof MethodDefinition ) {
			MethodDefinition def = ( MethodDefinition ) definition;
			
			String id = String.format( "method:%s:%s", def.getName( ), def.getSignature( ) );
			this.writeStyled( text, CSS.JAVA_DEF_METHOD );
		}
		else if( definition instanceof FieldDefinition ) {
			FieldDefinition def = ( FieldDefinition ) definition;
			
			String id = String.format( "field:%s:%s", def.getName( ), def.getSignature( ) );
			this.writeStyled( text, CSS.JAVA_DEF_FIELD );
		}
		else if( definition instanceof ParameterDefinition ) {
			this.writeStyled( text, CSS.JAVA_DEF_PARAMETER );
		}
		else if( definition instanceof VariableDefinition ) {
			this.writeStyled( text, CSS.JAVA_DEF_VARIABLE );
		}
		else {
			throw new IllegalArgumentException( "Can not write definition type: " + definition.getClass( ) );
		}
	}

	@Override
	public void writeReference( String text, Object reference ) {
		this.writeReference( text, reference, false );
	}

	@Override
	public void writeReference( String text, Object reference, boolean isLocal ) {
		if( reference instanceof PackageReference ) {
			this.write( text );
		}
		else if( reference instanceof TypeReference ) {
			TypeReference ref = ( TypeReference ) reference;
			if( text.equals( "@" ) ) {
				// Procyon has a bug where the '@' in front of attributes
				//  is written as an identifier. We manually handle this.
				this.write( text );
				return;
			}
			
			this.writeStyled( text, CSS.JAVA_REF_TYPE );
		}
		else if( reference instanceof MethodReference ) {
			MethodReference ref = ( MethodReference ) reference;

			this.writeStyled( text, CSS.JAVA_REF_METHOD );
		}
		else if( reference instanceof FieldReference ) {
			FieldReference ref = ( FieldReference ) reference;
			
			this.writeStyled( text, CSS.JAVA_REF_FIELD );
		}
		else if( reference instanceof ParameterReference ) {
//			ParameterReference ref = ( ParameterReference ) reference;
			
			this.writeStyled( text, CSS.JAVA_REF_PARAMETER );
		}
		else if( reference instanceof VariableReference ) {
			VariableReference ref = ( VariableReference ) reference;
			if( this.seenVariables.add( ref ) ) {
				// Procyon has a bug where both references and definitions
				//  for variables are passed to this method. We manually
				//  redirect definitions to the correct place.
				this.writeDefinition( text, ref, isLocal );
				return;
			}

			this.writeStyled( text, CSS.JAVA_REF_VARIABLE );
		}
		else {
			throw new IllegalArgumentException( "Can not write reference type: " + reference.getClass( ) );
		}
	}
	
	private void writeIndent( ) {
		if( this.needsIndent ) {
			for( int i = 0; i < this.indentDepth; i++ ) {
				this.writer.write( ' ' );
			}
			this.needsIndent = false;
		}
	}
	
	private void writeStyled( String text, String styleClasses ) {
		this.writeIndent( );
		
		int startOffset = this.writer.getBuffer( ).length( );
		this.writer.write( text );
		int endOffset = this.writer.getBuffer( ).length( );
		
		if( startOffset != endOffset ) {
			if( startOffset != this.lastStyledOffset ) {
				this.ssb.add( Collections.emptyList( ), startOffset - this.lastStyledOffset );
			}
			this.ssb.add( Arrays.asList( styleClasses.split( " " ) ), endOffset - startOffset );
			this.lastStyledOffset = endOffset;
		}
	}
	
	// Unimplemented methods of ITextOutput
	
	@Override
	public int getRow( ) {
		return 0;
	}

	@Override
	public int getColumn( ) {
		return 0;
	}
	
	@Override
	public String getIndentToken( ) {
		return null;
	}

	@Override
	public void setIndentToken( String indentToken ) { }
	
	@Override
	public boolean isFoldingSupported( ) {
		return false;
	}

	@Override
	public void markFoldStart( String collapsedText, boolean defaultCollapsed ) { }

	@Override
	public void markFoldEnd( ) { }
	
	@Override
	public void writeError( String value ) { }
	
}
