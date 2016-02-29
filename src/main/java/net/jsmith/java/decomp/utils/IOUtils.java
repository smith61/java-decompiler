package net.jsmith.java.decomp.utils;

public class IOUtils {

	public static void safeClose( AutoCloseable c ) {
		if( c != null ) {
			try {
				c.close( );
			}
			catch( Exception e ) { }
		}
	}
	
}
