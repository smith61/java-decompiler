package net.jsmith.java.decomp.repository;

import java.util.Set;

import com.strobel.assembler.metadata.ITypeLoader;

public interface ClassRepository extends ITypeLoader {
	
	String getName( );
	
	Set< String > getResolvableTypes( );

}
