package net.jsmith.java.decomp.utils;

import java.util.AbstractList;
import java.util.stream.Stream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLStreamSupport {

	public static Stream< Node > stream( final NodeList nodelist ) {
		return new AbstractList< Node >( ) {

			@Override
			public Node get( int index ) {
				return nodelist.item( index );
			}

			@Override
			public int size( ) {
				return nodelist.getLength( );
			}
			
		}.stream( );
	}
	
}
