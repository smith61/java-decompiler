package net.jsmith.java.decomp.workspace.impl;

import java.util.Objects;

import org.objectweb.asm.MethodVisitor;

import net.jsmith.java.decomp.workspace.MethodReference;

public class MethodFilter extends Filter {
	
	private final MethodReference method;
	
	private boolean accept;
	
	public MethodFilter( MethodReference method ) {
		super( ASM5 );
		
		this.method = Objects.requireNonNull( method, "method" );
		
		this.accept = false;
	}

	@Override
	public boolean accept( ) {
		return this.accept;
	}

	@Override
	public MethodVisitor visitMethod( int access, String name, String desc, String signature, String[ ] exceptions ) {
		if( name.equals( this.method.getMethodName( ) ) ) {
			String methodSig = this.method.getMethodSignature( );
			// We attempt to match against either desc or signature
			//  as we may be resolving a generic type signature.
			if( methodSig.equals( desc ) || methodSig.equals( signature ) ) {
				this.accept = true;
			}
		}
		return null;
	}
	
	
	
}
