package net.jsmith.java.byteforge.workspace;

public class Modifier extends java.lang.reflect.Modifier {

    public static final int BRIDGE      = 0x00000040;
    public static final int VARARGS     = 0x00000080;
    public static final int SYNTHETIC   = 0x00001000;
    public static final int ANNOTATION  = 0x00002000;
    public static final int ENUM        = 0x00004000;
	
    public static boolean isBridge( int mod ) {
    	return isSet( mod, BRIDGE );
    }
    
    public static boolean isVarargs( int mod ) {
    	return isSet( mod, VARARGS );
    }
    
    public static boolean isSynthetic( int mod ) {
    	return isSet( mod, SYNTHETIC );
    }
    
    public static boolean isAnnotation( int mod ) {
    	return isSet( mod, ANNOTATION );
    }
    
    public static boolean isEnum( int mod ) {
    	return isSet( mod, ENUM );
    }
    
	private static boolean isSet( int mod, int flags ) {
		return ( mod & flags ) == flags;
	}
	
}
