module net.jsmith.java.byteforge {
	requires com.google.common;
	requires jdk.jsobject;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;
	requires org.objectweb.asm;
	requires procyon.compilertools;
	requires slf4j.api;
	
	opens net.jsmith.java.byteforge.gui.controllers;
	opens net.jsmith.java.byteforge.gui.controls;
	opens net.jsmith.java.byteforge.utils;

	exports net.jsmith.java.byteforge.gui;
}