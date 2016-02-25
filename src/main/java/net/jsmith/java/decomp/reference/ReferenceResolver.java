package net.jsmith.java.decomp.reference;

import java.util.List;

public interface ReferenceResolver {
    
    String getName( );
    
    List< TypeReference > resolveTypeReference( String typeName );
    
}
