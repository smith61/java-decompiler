package net.jsmith.java.byteforge.workspace.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.jsmith.java.byteforge.utils.TypeNameUtils;
import net.jsmith.java.byteforge.workspace.Metadata;

public class MetadataImpl implements Metadata {

	private final String fullName;
	
	private final String packageName;
	private final String typeName;
	
	private final String enclosingType;
	private final String enclosingMethodName;
	private final String enclosingMethodSignature;
	
	private final List< String > enclosingTypes;
	
	private final int modifiers;
	
	public MetadataImpl( String fullName, String enclosingType, String enclosingMethodName, String enclosingMethodSignature, List< String > enclosingTypes, int modifiers ) {
		this.fullName = Objects.requireNonNull( fullName, "fullName" );
		
		this.enclosingType = enclosingType;
		this.enclosingMethodName = enclosingMethodName;
		this.enclosingMethodSignature = enclosingMethodSignature;
		
		this.enclosingTypes = Objects.requireNonNull( enclosingTypes, "enclosingTypes" );
		
		this.modifiers = modifiers;
		
		this.packageName = TypeNameUtils.getPackageName( fullName );
		this.typeName = TypeNameUtils.getTypeName( fullName );
	}
	
	@Override
	public String getFullName( ) {
		return this.fullName;
	}

	@Override
	public String getPackageName( ) {
		return this.packageName;
	}

	@Override
	public String getTypeName( ) {
		return this.typeName;
	}

	@Override
	public int getModifiers( ) {
		return this.modifiers;
	}

	@Override
	public String getEnclosingType( ) {
		return this.enclosingType;
	}

	@Override
	public String getEnclosingMethodName( ) {
		return this.enclosingMethodName;
	}

	@Override
	public String getEnclosingMethodSignature( ) {
		return this.enclosingMethodSignature;
	}

	@Override
	public List< String > getEnclosedTypes( ) {
		return Collections.unmodifiableList( this.enclosingTypes );
	}
	
	public static Builder builder( ) {
		return new Builder( );
	}

	public static class Builder {
		
		private String fullName;
		
		private String enclosingType;
		private String enclosingMethodName;
		private String enclosingMethodSignature;
		
		private List< String > enclosedTypes;
		
		private int modifiers;
		
		public Builder( ) {
			this.enclosedTypes = new ArrayList< >( );
		}
		
		public Builder setFullName( String fullName ) {
			this.fullName = fullName;
			return this;
		}
		
		public Builder setEnclosingType( String enclosingType ) {
			this.enclosingType = enclosingType;
			return this;
		}
		
		public Builder setEnclosingMethod( String enclosingMethodName, String enclosingMethodSignature ) {
			this.enclosingMethodName = enclosingMethodName;
			this.enclosingMethodSignature = enclosingMethodSignature;
			return this;
		}
		
		public Builder addEnclosedType( String enclosedType ) {
			this.enclosedTypes.add( enclosedType );
			return this;
		}
		
		public Builder setModifiers( int modifiers ) {
			this.modifiers = modifiers;
			return this;
		}
		
		public MetadataImpl build( ) {
			return new MetadataImpl( fullName, enclosingType, enclosingMethodName, enclosingMethodSignature, enclosedTypes, modifiers );
		}
		
	}
	
}
