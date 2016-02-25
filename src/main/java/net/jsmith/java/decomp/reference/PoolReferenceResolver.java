package net.jsmith.java.decomp.reference;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PoolReferenceResolver implements ReferenceResolver {
    
    private final Set< ReferenceResolver > pooledResolvers;
    
    public PoolReferenceResolver( ) {
        this.pooledResolvers = new HashSet< >( );
    }
    
    public void addResolver( ReferenceResolver resolver ) {
        this.pooledResolvers.add( Objects.requireNonNull( resolver, "resolver" ) );
    }
    
    public void removeResolver( ReferenceResolver resolver ) {
        this.pooledResolvers.remove( Objects.requireNonNull( resolver, "resolver" ) );
    }
    
    @Override
    public String getName( ) {
        return this.pooledResolvers.stream( ).map( Object::toString ).collect( Collectors.joining( ", " ) );
    }
    
    @Override
    public List< TypeReference > resolveTypeReference( String typeName ) {
        return this.pooledResolvers.stream( ).map( ( resolver ) -> {
            return resolver.resolveTypeReference( typeName );
        } ).flatMap( List::stream ).collect( Collectors.toList( ) );
    }
    
}
