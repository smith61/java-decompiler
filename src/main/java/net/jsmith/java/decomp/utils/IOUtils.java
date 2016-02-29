package net.jsmith.java.decomp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	public static void safeClose( AutoCloseable c ) {
		if( c != null ) {
			try {
				c.close( );
			}
			catch( Exception e ) { }
		}
	}
	
	public static byte[ ] readFully( InputStream is ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );
		copyStream( is, baos );
		
		return baos.toByteArray( );
	}
	
	public static void copyStream( InputStream is, OutputStream os ) throws IOException {
		byte[ ] buffer = new byte[ 4096 ];
		int read;
		
		while( ( read = is.read( buffer ) ) >= 0 ) {
			os.write( buffer, 0, read );
		}
	}
	
}
