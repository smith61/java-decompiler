package net.jsmith.java.decomp.decompiler.procyon;

import java.util.HashSet;
import java.util.Set;

import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;

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
import net.jsmith.java.decomp.decompiler.HtmlRenderer;

public class JavaHtmlOutput extends HtmlRenderer implements ITextOutput {

	private final boolean isRootRender;
	
	private final Set< VariableReference > seenVariables;
	
	public JavaHtmlOutput( ) {
		this( new HtmlCanvas( ), true );
	}
	
	public JavaHtmlOutput( HtmlCanvas canvas ) {
		this( canvas, false );
	}
	
	protected JavaHtmlOutput( HtmlCanvas canvas, boolean isRootRender ) {
		super( canvas );
		this.isRootRender = isRootRender;
		
		this.seenVariables = new HashSet< >( );
		
		if( this.isRootRender ) {
			this.render( ( html ) -> {
				html.html( ).body( );
			} );
		}
	}
	
	@Override
	public String getHtml( ) {
		if( this.isRootRender ) {
			this.render( ( canvas ) -> {
				canvas._body( )._html( );
			} );
		}
		return super.getHtml( );
	}

	@Override
	public void indent( ) {
		this.render( ( canvas ) -> {
			canvas.div( class_( CSS.JAVA_INDENT ) );
		} );
	}

	@Override
	public void unindent( ) {
		this.render( ( canvas ) -> {
			canvas._div( );
		} );
	}

	@Override
	public void write( char ch ) {
		this.render( ( canvas ) -> {
			canvas.write( "" + ch );
		} );
	}

	@Override
	public void write( String text ) {
		this.render( ( canvas ) -> {
			canvas.write( text );
		} );
	}

	@Override
	public void writeLabel( String value ) {
		this.render( ( canvas ) -> {
			canvas.span( class_( CSS.JAVA_LABEL ) ).content( value );
		} );
	}

	@Override
	public void writeLiteral( Object value ) {
		this.render( ( canvas ) -> {
			canvas.span( class_( CSS.JAVA_LITERAL_NUMBER ) ).content( value.toString( ) );
		} );
	}

	@Override
	public void writeTextLiteral( Object value ) {
		this.render( ( canvas ) -> {
			canvas.span( class_( CSS.JAVA_LITERAL_TEXT ) ).content( value.toString( ) );
		} );
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
		this.render( ( canvas ) -> {
			canvas.br( );
		} );
	}
	
	@Override
	public void writeComment( String value ) {
		this.render( ( canvas ) -> {
			canvas.span( class_( CSS.JAVA_COMMENT ) ).content( value );
		} );
	}

	@Override
	public void writeComment( String format, Object... args ) {
		this.writeComment( String.format( format, args ) );
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
	public void writeKeyword( String keyword ) {
		this.render( ( canvas ) -> {
			canvas.span( class_( CSS.JAVA_KEYWORD( keyword ) ) ).content( keyword );
		} );
	}

	@Override
	public void writeAttribute( String text ) {
		this.write( text );
	}

	@Override
	public void writeDefinition( String text, Object definition ) {
		this.writeDefinition( text, definition, true );
	}

	@Override
	public void writeDefinition( String text, Object definition, boolean isLocal ) {
		this.render( ( canvas ) -> {
			if( definition instanceof TypeDefinition ) {
				TypeDefinition def = ( TypeDefinition ) definition;
				
				String id = String.format( "type:%s", def.getFullName( ) );
				canvas.span( class_( CSS.JAVA_DEF_TYPE ).id( id ) ).content( text );
			}
			else if( definition instanceof MethodDefinition ) {
				MethodDefinition def = ( MethodDefinition ) definition;
				
				String id = String.format( "method:%s:%s", def.getName( ), def.getSignature( ) );
				canvas.span( class_( CSS.JAVA_DEF_METHOD ).id( id ) ).content( text );
			}
			else if( definition instanceof FieldDefinition ) {
				FieldDefinition def = ( FieldDefinition ) definition;
				
				String id = String.format( "field:%s:%s", def.getName( ), def.getSignature( ) );
				canvas.span( class_( CSS.JAVA_DEF_FIELD ).id( id ) ).content( text );
			}
			else if( definition instanceof ParameterDefinition ) {
				canvas.span( class_( CSS.JAVA_DEF_PARAMETER ) ).content( text );
			}
			else if( definition instanceof VariableDefinition ) {
				canvas.span( class_( CSS.JAVA_DEF_VARIABLE ) ).content( text );
			}
			else {
				throw new IllegalArgumentException( "Can not write definition type: " + definition.getClass( ) );
			}
		} );
	}

	@Override
	public void writeReference( String text, Object reference ) {
		this.writeReference( text, reference, false );
	}

	@Override
	public void writeReference( String text, Object reference, boolean isLocal ) {
		this.render( ( canvas ) -> {
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
				
				HtmlAttributes attribs = class_( CSS.JAVA_REF_TYPE );
				attribs.add( "ref_type", "type" );
				attribs.add( "type", ref.getInternalName( ).replace( '/', '.' ) );
				
				canvas.span( attribs ).content( text );
			}
			else if( reference instanceof MethodReference ) {
				MethodReference ref = ( MethodReference ) reference;
				
				HtmlAttributes attribs = class_( CSS.JAVA_REF_METHOD );
				attribs.add( "ref_type", "method" );
				attribs.add( "type", ref.getDeclaringType( ).getFullName( ) );
				attribs.add( "method_name", ref.getName( ) );
				attribs.add( "method_type", ref.getReturnType( ).getFullName( ) );
				attribs.add( "method_sig", ref.getSignature( ) );
				
				canvas.span( attribs ).content( text );
			}
			else if( reference instanceof FieldReference ) {
				FieldReference ref = ( FieldReference ) reference;
				
				HtmlAttributes attribs = class_( CSS.JAVA_REF_FIELD );
				attribs.add( "ref_type", "field" );
				attribs.add( "type", ref.getDeclaringType( ).getFullName( ) );
				attribs.add( "field_name", ref.getName( ) );
				attribs.add( "field_type", ref.getFieldType( ).getFullName( ) );
				
				canvas.span( attribs ).content( text );
			}
			else if( reference instanceof ParameterReference ) {
//				ParameterReference ref = ( ParameterReference ) reference;
				
				canvas.span( class_( CSS.JAVA_REF_PARAMETER ) ).content( text );
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
				
				canvas.span( class_( CSS.JAVA_REF_VARIABLE ) ).content( text );
			}
			else {
				throw new IllegalArgumentException( "Can not write reference type: " + reference.getClass( ) );
			}
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
