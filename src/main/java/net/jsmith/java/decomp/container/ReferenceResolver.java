package net.jsmith.java.decomp.container;

import java.util.List;

public interface ReferenceResolver {

	String getName( );
	
	List< Type > resolveType( String typeName );
	
}
