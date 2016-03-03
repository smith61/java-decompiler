package net.jsmith.java.decomp.workspace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jsmith.java.decomp.utils.IOUtils;
import net.jsmith.java.decomp.workspace.FieldReference;
import net.jsmith.java.decomp.workspace.MethodReference;
import net.jsmith.java.decomp.workspace.Reference;
import net.jsmith.java.decomp.workspace.Type;
import net.jsmith.java.decomp.workspace.TypeReference;

public class ReferenceResolver {
	
	private static final Logger LOG = LoggerFactory.getLogger( ReferenceResolver.class );
	
	private final WorkspaceImpl workspace;
	private final Reference reference;
	
	public ReferenceResolver( WorkspaceImpl workspace, Reference reference ) {
		this.workspace = Objects.requireNonNull( workspace, "workspace" );
		this.reference = Objects.requireNonNull( reference, "reference" );
	}
	
	public List< Type > resolve( ) {
		switch( reference.getReferenceType( ) ) {
			case TYPE:
				return resolveTypeReference( );
			case FIELD:
				return resolveFieldReference( );
			case METHOD:
				return resolveMethodReference( );
			default:
				throw new IllegalStateException( "Illegal reference type: " + reference.getReferenceType( ) );
		}
	}
	
	private List< Type > resolveTypeReference( ) {
		TypeReference tr = ( TypeReference ) this.reference;
		
		return this.getAllTypes( tr.getTypeName( ) );
	}
	
	private List< Type > resolveFieldReference( ) {
		FieldReference fr = ( FieldReference ) this.reference;
		
		return this.getAllTypes( fr.getDeclaringType( ) ).stream( ).filter( ( type ) -> {
			return this.accept( type, new FieldFilter( fr ) );
		} ).collect( Collectors.toList( ) );
	}
	
	private List< Type > resolveMethodReference( ) {
		MethodReference mr = ( MethodReference ) this.reference;
		
		return this.getAllTypes( mr.getDeclaringType( ) ).stream( ).filter( ( type ) -> {
			return this.accept( type, new MethodFilter( mr ) );
		} ).collect( Collectors.toList( ) );
	}
	
	private List< Type > getAllTypes( String typeName ) {
		return this.workspace.getContainers( ).stream( ).map( ( container ) -> {
			return container.findType( typeName );
		} ).filter( ( type ) -> {
			return type != null;
		} ).collect( Collectors.toList( ) );
	}
	
	private boolean accept( Type type, Filter filter ) {
		InputStream is = null;
		try {
			is = type.getInputStream( );
			new ClassReader( is ).accept( filter, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES );
			
			return filter.accept( );
		}
		catch( IOException ioe ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error loading input stream for type '{}' in container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ), ioe );
			}
			return false;
		}
		finally {
			IOUtils.safeClose( is );
		}
	}
	
}
