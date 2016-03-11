package net.jsmith.java.byteforge.utils;

public class TypeNameUtils {
	
	public static final char PACKAGE_SEPARATOR = '/';
	public static final char TYPE_SEPARATOR    = '$';

	public static String getPackageName( String internalName ) {
		int endIndex = internalName.lastIndexOf( PACKAGE_SEPARATOR );
		if( endIndex < 0 ) return "";
		return internalName.substring( 0, endIndex );
	}

	public static String getTypeName( String internalName ) {
		int startIndex = internalName.lastIndexOf( PACKAGE_SEPARATOR );
		if( startIndex < 0 ) return internalName;
		return internalName.substring( startIndex + 1 );
	}
	
	public static String getEnclosingTypeName( String internalName ) {
		int endIndex = internalName.lastIndexOf( TYPE_SEPARATOR );
		if( endIndex <= 0 ) return null;
		// Some classes, specifically those in Gson, decide
		//  to use the TYPE_SEPARATOR in actual names of types
		//  that are not embedded in other types. This semi-protects
		//  from that but may return false enclosing types.
		if( internalName.charAt( endIndex - 1 ) == PACKAGE_SEPARATOR ) return null;
		return internalName.substring( 0, endIndex );
	}
	
	public static String[ ] getPackageParts( String internalName ) {
		String packageName = getPackageName( internalName );
		if( packageName.isEmpty( ) ) return new String[ 0 ];
		return packageName.split( "" + PACKAGE_SEPARATOR );
	}
	
	public static String[ ] getTypeParts( String internalName ) {
		String typeName = getTypeName( internalName );
		if( typeName.isEmpty( ) ) return new String[ 0 ];
		return typeName.split( "\\" + TYPE_SEPARATOR );
	}
	
}
