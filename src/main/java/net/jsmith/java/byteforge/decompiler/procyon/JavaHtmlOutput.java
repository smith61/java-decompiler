package net.jsmith.java.byteforge.decompiler.procyon;

import java.util.HashSet;
import java.util.Set;

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

import net.jsmith.java.byteforge.decompiler.CSS;
import net.jsmith.java.byteforge.decompiler.HtmlAttributes;
import net.jsmith.java.byteforge.decompiler.HtmlCanvas;
import net.jsmith.java.byteforge.decompiler.HtmlRenderer;

public class JavaHtmlOutput extends HtmlRenderer implements ITextOutput {
	
	private static final int INDENT_DEPTH = 4;
	
	private final Set< VariableReference > seenVariables;
	
	private boolean needsIndent;
	private int indentDepth;
	
	public JavaHtmlOutput( ) {
		super( new HtmlCanvas( ) );
		
		this.seenVariables = new HashSet< >( );
	
		this.needsIndent = false;
		this.indentDepth = 0;
		
		this.render( ( html ) -> {
			html.html( ).body( ).pre( );
		} );
		this.openLine( );
	}
	
	@Override
	public String getHtml( ) {
		this.closeLine( );
		this.render( ( canvas ) -> {
			canvas._pre( )._body( )._html( );
		} );
		return super.getHtml( );
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
		this.render( ( canvas ) -> {
			canvas.write( text );
		} );
	}
	
	@Override
	public void write( String format, Object... args ) {
		this.write( String.format( format, args ) );
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
		this.closeLine( );
		this.render( ( canvas ) -> {
			canvas.write( '\n' );
			
			this.needsIndent = true;
		} );
		this.openLine( );
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
		this.writeStyled( value.toString( ), CSS.JAVA_COMMENT );
	}

	@Override
	public void writeComment( String format, Object... args ) {
		this.writeComment( String.format( format, args ) );
	}

	@Override
	public void writeKeyword( String keyword ) {
		this.writeStyled( keyword, CSS.JAVA_KEYWORD( keyword ) );
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
			HtmlAttributes attributes = new HtmlAttributes();
			attributes.add("class", CSS.JAVA_DEF_TYPE);
			attributes.add("id", id);
			this.writeStyled(text, attributes);
		}
		else if( definition instanceof MethodDefinition ) {
			MethodDefinition def = ( MethodDefinition ) definition;
			
			String id = String.format( "method:%s:%s:%s", def.getDeclaringType( ).getInternalName( ), def.getName( ), def.getSignature( ) );
			HtmlAttributes attributes = new HtmlAttributes();
			attributes.add("class", CSS.JAVA_DEF_TYPE);
			attributes.add("id", id);
			this.writeStyled(text, attributes);
		}
		else if( definition instanceof FieldDefinition ) {
			FieldDefinition def = ( FieldDefinition ) definition;
			
			String id = String.format( "field:%s:%s:%s", def.getDeclaringType( ).getInternalName( ), def.getName( ), def.getSignature( ) );
			HtmlAttributes attributes = new HtmlAttributes();
			attributes.add("class", CSS.JAVA_DEF_TYPE);
			attributes.add("id", id);
			this.writeStyled(text, attributes);
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
			
			HtmlAttributes attribs = new HtmlAttributes();
			attribs.add("class", CSS.JAVA_REF_TYPE);
			attribs.add( "ref_type", "type" );
			attribs.add( "type", ref.getInternalName( ) );
			
			this.writeStyled( text, attribs );
		}
		else if( reference instanceof MethodReference ) {
			MethodReference ref = ( MethodReference ) reference;
			
			HtmlAttributes attribs =  new HtmlAttributes();
			attribs.add("class", CSS.JAVA_REF_METHOD);
			attribs.add( "ref_type", "method" );
			attribs.add( "type", ref.getDeclaringType( ).getInternalName( ) );
			attribs.add( "method_name", ref.getName( ) );
			attribs.add( "method_type", ref.getReturnType( ).getInternalName( ) );
			attribs.add( "method_sig", ref.getSignature( ) );

			this.writeStyled( text, attribs );
		}
		else if( reference instanceof FieldReference ) {
			FieldReference ref = ( FieldReference ) reference;
			
			HtmlAttributes attribs =  new HtmlAttributes();
			attribs.add("class", CSS.JAVA_REF_FIELD);
			attribs.add( "ref_type", "field" );
			attribs.add( "type", ref.getDeclaringType( ).getInternalName( ) );
			attribs.add( "field_name", ref.getName( ) );
			attribs.add( "field_type", ref.getFieldType( ).getSignature( ) );

			this.writeStyled( text, attribs );
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
			this.render( ( canvas ) -> {
				for( int i = 0; i < this.indentDepth; i++ ) {
					canvas.write( ' ' );
				}
			} );
			this.needsIndent = false;
		}
	}
	
	private void writeStyled( String text, String styleClasses ) {
		HtmlAttributes attribs =  new HtmlAttributes();
		attribs.add("class", styleClasses);
		this.writeStyled( text, attribs );
	}
	
	private void writeStyled( String text, HtmlAttributes attribs ) {
		this.writeIndent( );
		
		this.render( ( canvas ) -> {
			canvas.span( attribs ).content( text )._span();
		} );
	}
	
	private void openLine( ) {
		this.render( ( canvas ) -> {
			HtmlAttributes attributes = new HtmlAttributes();
			attributes.add("class", CSS.JAVA_LINE);
			canvas.span(attributes);
		} );
	}
	
	private void closeLine( ) {
		this.render( ( canvas ) -> {
			canvas._span( );
		} );
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
