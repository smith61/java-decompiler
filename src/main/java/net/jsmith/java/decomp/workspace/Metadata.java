package net.jsmith.java.decomp.workspace;

import java.util.List;

public interface Metadata {

	String getFullName( );
	
	String getPackageName( );
	
	String getTypeName( );
	
	int getModifiers( );
	
	String getEnclosingType( );
	
	String getEnclosingMethodName( );
	
	String getEnclosingMethodSignature( );
	
	List< String > getEnclosedTypes( );
	
}
