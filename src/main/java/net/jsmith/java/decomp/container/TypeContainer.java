package net.jsmith.java.decomp.container;

import java.io.Closeable;

import javafx.collections.ObservableMap;

public interface TypeContainer extends ReferenceResolver, Closeable {
	
	ObservableMap< String, Type > getContainedTypes( );
	
}
