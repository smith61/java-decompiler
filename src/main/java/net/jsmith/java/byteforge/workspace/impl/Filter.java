package net.jsmith.java.byteforge.workspace.impl;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public abstract class Filter extends ClassVisitor implements Opcodes {

	public Filter( int api, ClassVisitor cv ) {
		super( api, cv );
	}
	
	public Filter( int api ) {
		super( api );
	}
	
	public abstract boolean accept( );
	
}
