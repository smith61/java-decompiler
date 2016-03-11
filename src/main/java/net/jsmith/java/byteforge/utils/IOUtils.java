package net.jsmith.java.byteforge.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtils {

	public static void safeClose( AutoCloseable c ) {
		if( c != null ) {
			try {
				c.close( );
			}
			catch( Exception e ) { }
		}
	}
	
	public static String readResourceAsString( String resourceName ) throws IOException{
		return new String( IOUtils.readResource( resourceName ), Charset.forName( "UTF-8" ) );
	}
	
	public static byte[ ] readResource( String resourceName ) throws IOException {
		InputStream is = null;
		try {
			is = IOUtils.class.getResourceAsStream( resourceName );
			if( is == null ) {
				throw new IOException( "Resource not found: " + resourceName );
			}
			return readFully( is );
		}
		finally {
			IOUtils.safeClose( is );
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
