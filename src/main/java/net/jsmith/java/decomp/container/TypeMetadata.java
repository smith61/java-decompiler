package net.jsmith.java.decomp.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.strobel.assembler.metadata.Flags;

public class TypeMetadata {

	private final String internalName;
	private final int flags;
	
	private final String enclosingType;
	
	private final String enclosingMethod;
	private final String enclosingMethodDesc;
	
	private final List< String > enclosedTypes;
	
	public TypeMetadata( String internalName, int flags, String enclosingType, String enclosingMethod, String enclosingMethodDesc, List< String > enclosedTypes ) {
		this.internalName = Objects.requireNonNull( internalName, "internalName" );
		this.flags = flags;
		
		this.enclosingType = enclosingType;
		
		this.enclosingMethod = enclosingMethod;
		this.enclosingMethodDesc = enclosingMethodDesc;
		
		this.enclosedTypes = Objects.requireNonNull( enclosedTypes, "enclosedTypes" );
	}
	
	public String getFullName( ) {
		return this.internalName;
	}
	
	public String getPackage( ) {
		int endIndex = this.internalName.lastIndexOf( '.' );
		if( endIndex < 0 ) {
			return "";
		}
		return this.internalName.substring( 0, endIndex );
	}
	
	public String getTypeName( ) {
		int startIndex = this.internalName.lastIndexOf( '.' );
		if( startIndex < 0 ) {
			return this.internalName;
		}
		return this.internalName.substring( startIndex + 1 );
	}
	
	public int getFlags( ) {
		return this.flags;
	}
	
	public boolean isPublic( ) {
		return Flags.testAll( this.getFlags( ), Flags.PUBLIC );
	}
	
	public boolean isPrivate( ) {
		return Flags.testAll( this.getFlags( ), Flags.PRIVATE );
	}
	
	public boolean isProtected( ) {
		return Flags.testAll( this.getFlags( ), Flags.PROTECTED );
	}
	
	public boolean isStatic( ) {
		return Flags.testAll( this.getFlags( ), Flags.STATIC );
	}
	
	public boolean isAnnotation( ) {
		return this.isInterface( ) && Flags.testAll( this.getFlags( ), Flags.ANNOTATION );
	}
	
	public boolean isInterface( ) {
		return Flags.testAll( this.getFlags( ), Flags.INTERFACE );
	}
	
	public boolean isEnum( ) {
		return Flags.testAll( this.getFlags( ), Flags.ENUM );
	}
	
	public boolean isClass( ) {
		return !this.isInterface( ) && !this.isEnum( );
	}
	
	public boolean isAnonymous( ) {
		return this.getEnclosingMethod( ) != null;
	}
	
	public String getEnclosingType( ) {
		return this.enclosingType;
	}
	
	public String getEnclosingMethod( ) {
		return this.enclosingMethod;
	}
	
	public String getEnclosingMethodDesc( ) {
		return this.enclosingMethodDesc;
	}
	
	public List< String > getEnclosedTypes( ) {
		return Collections.unmodifiableList( this.enclosedTypes );
	}
	
	public static Builder builder( ) {
		return new Builder( );
	}
	
	public static class Builder {
		
		private String typeName;
		private int flags;
		
		private String enclosingType;
		
		private String enclosingMethod;
		private String enclosingMethodDesc;
		
		private List< String > enclosedTypes;
		
		public Builder( ) {
			this.enclosedTypes = new ArrayList< >( );
		}
		
		public Builder setTypeName( String typeName ) {
			this.typeName = typeName;
			return this;
		}
		
		public Builder setFlags( int flags ) {
			this.flags = flags;
			return this;
		}
		
		public Builder setEnclosingType( String enclosingType ) {
			this.enclosingType = enclosingType;
			return this;
		}
		
		public Builder setEnclosingMethod( String enclosingMethod ) {
			this.enclosingMethod = enclosingMethod;
			return this;
		}
		
		public Builder setEnclosingMethodDesc( String enclosingMethodDesc ) {
			this.enclosingMethodDesc = enclosingMethodDesc;
			return this;
		}
		
		public Builder addEnclosedType( String enclosedType ) {
			this.enclosedTypes.add( enclosedType );
			return this;
		}
		
		public TypeMetadata build( ) {
			return new TypeMetadata( this.typeName, this.flags, this.enclosingType, this.enclosingMethod, this.enclosingMethodDesc, this.enclosedTypes );
		}
		
	}
	
}
