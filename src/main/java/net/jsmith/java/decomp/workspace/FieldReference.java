package net.jsmith.java.decomp.workspace;

import java.util.Objects;

public class FieldReference extends Reference {
	
	private final String declaringType;
	
	private final String fieldName;
	private final String fieldType;
	
	public FieldReference( String declaringType, String fieldName, String fieldType ) {
		super( Type.FIELD );
		
		this.declaringType = Objects.requireNonNull( declaringType, "declaringType" );
		
		this.fieldName = Objects.requireNonNull( fieldName, "fieldName" );
		this.fieldType = Objects.requireNonNull( fieldType, "fieldType" );
	}

	public String getDeclaringType( ) {
		return declaringType;
	}

	public String getFieldName( ) {
		return fieldName;
	}

	public String getFieldType( ) {
		return fieldType;
	}

	@Override
	public String toAnchorID( ) {
		return String.format( "field:%s:%s:%s", this.declaringType, this.fieldName, this.fieldType );
	}
	
}
