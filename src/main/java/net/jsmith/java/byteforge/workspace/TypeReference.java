package net.jsmith.java.byteforge.workspace;

import java.util.Objects;

public class TypeReference extends Reference {
	
	private final String typeName;

	public TypeReference( String typeName ) {
		super( Type.TYPE );
		
		this.typeName = Objects.requireNonNull( typeName, "typeName" );
	}
	
	public String getTypeName( ) {
		return this.typeName;
	}
	
	@Override
	public String toAnchorID( ) {
		return String.format( "type:%s", this.typeName );
	}
	
}
