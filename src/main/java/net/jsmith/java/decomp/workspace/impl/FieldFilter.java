package net.jsmith.java.decomp.workspace.impl;

import java.util.Objects;

import org.objectweb.asm.FieldVisitor;

import net.jsmith.java.decomp.workspace.FieldReference;

public class FieldFilter extends Filter {

	private final FieldReference field;
	
	private boolean accept;
	
	public FieldFilter( FieldReference fieldReference ) {
		super( ASM5 );
		
		this.field = Objects.requireNonNull( fieldReference, "fieldReference" );
		
		this.accept = false;
	}

	@Override
	public boolean accept( ) {
		return this.accept;
	}

	@Override
	public FieldVisitor visitField( int access, String name, String desc, String signature, Object value ) {
		if( name.equals( this.field.getFieldName( ) ) ) {
			String fieldType = this.field.getFieldType( );
			if( fieldType.equals( desc ) || fieldType.equals( signature ) ) {
				this.accept = true;
			}
		}
		
		return null;
	}
	
	
	
}
