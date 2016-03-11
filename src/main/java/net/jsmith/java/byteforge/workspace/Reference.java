package net.jsmith.java.byteforge.workspace;

import java.util.Objects;

public abstract class Reference {
	
	private final Type type;
	
	protected Reference( Type type ) {
		this.type = Objects.requireNonNull( type, "type" );
	}
	
	public final Type getReferenceType( ) {
		return this.type;
	}
	
	public abstract String toAnchorID( );
	
	public static enum Type {
		
		TYPE,
		FIELD,
		METHOD;
		
	}
	
}
