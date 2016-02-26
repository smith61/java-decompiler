package net.jsmith.java.decomp.container;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupReferenceResolver implements ReferenceResolver {

	private final Set< ReferenceResolver > resolvers;
	
	public GroupReferenceResolver( ) {
		this.resolvers = new HashSet< >( );
	}
	
	public void addResolver( ReferenceResolver resolver ) {
		this.resolvers.add( resolver );
	}
	
	public void removeResolver( ReferenceResolver resolver ) {
		this.resolvers.remove( resolver );
	}

	@Override
	public String getName( ) {
		String joinedNames = this.resolvers.stream( ).map( ReferenceResolver::getName ).collect( Collectors.joining( ", " ) );
		return String.format( "[ %s ]", joinedNames );
	}

	@Override
	public List< Type > resolveType( String typeName ) {
		return this.resolvers.stream( ).map( ( resolver ) -> {
			return resolver.resolveType( typeName );
		} ).flatMap( List::stream ).collect( Collectors.toList( ) );
	}
	
}
