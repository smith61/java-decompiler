package net.jsmith.java.byteforge.workspace;

import java.util.Objects;

public class MethodReference extends Reference {
	
	private final String declaringType;
	
	private final String methodName;
	private final String methodSignature;
	
	public MethodReference( String declaringType, String methodName, String methodSignature ) {
		super( Type.METHOD );
		
		this.declaringType = Objects.requireNonNull( declaringType, "declaringType" );
		
		this.methodName = Objects.requireNonNull( methodName, "methodName" );
		this.methodSignature = Objects.requireNonNull( methodSignature, "methodSignature" );
	}

	public String getDeclaringType( ) {
		return declaringType;
	}

	public String getMethodName( ) {
		return methodName;
	}

	public String getMethodSignature( ) {
		return methodSignature;
	}

	@Override
	public String toAnchorID( ) {
		return String.format( "method:%s:%s:%s", this.declaringType, this.methodName, this.methodSignature );
	}
	
}
