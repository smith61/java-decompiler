package net.jsmith.java.byteforge.gui.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.jsmith.java.byteforge.decompiler.DecompilerUtils;
import net.jsmith.java.byteforge.gui.ErrorDialog;
import net.jsmith.java.byteforge.utils.ThreadPools;
import net.jsmith.java.byteforge.workspace.Reference;
import net.jsmith.java.byteforge.workspace.Type;

public class TypeViewState {

	private static final Logger LOG = LoggerFactory.getLogger( TypeViewState.class );
	
	private final Type type;
	
	private final BooleanProperty isDecompiled;
	
	private String decompiledContent;
	
	private Reference seekReference;
	private int seekLocation;
	
	public TypeViewState( Type type ) {
		this.type = Objects.requireNonNull( type, "type" );
		
		this.isDecompiled = new SimpleBooleanProperty( false );
		
		this.decompiledContent = String.format( "Decompiling type '%s'...", type.getMetadata( ).getFullName( ) );
		
		this.seekReference = null;
		this.seekLocation = 0;
		
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Decompiling type '{}' of container '{}'.", this.type.getMetadata( ).getFullName( ),
					this.type.getContainer( ).getName( ) );
		}
		DecompilerUtils.defaultDecompile( type ).whenCompleteAsync( this::onContentRecieved, ThreadPools.PLATFORM );
	}
	
	public Type getType( ) {
		return this.type;
	}
	
	public boolean getIsDecompiled( ) {
		return this.isDecompiled.get( );
	}
	
	public ReadOnlyBooleanProperty getIsDecompiledProperty( ) {
		return this.isDecompiled;
	}
	
	public String getDecompiledContent( ) {
		return this.decompiledContent;
	}
	
	public Reference getSeekReference( ) {
		return this.seekReference;
	}
	
	public void setSeekReference( Reference seekReference ) {
		this.seekReference = seekReference;
	}
	
	public int getSeekLocation( ) {
		return this.seekLocation;
	}
	
	public void setSeekLocation( int seekLocation ) {
		this.seekLocation = seekLocation;
	}
	
	private void onContentRecieved( String content, Throwable err ) {
		if( err != null ) {
			if( LOG.isErrorEnabled( ) ) {
				LOG.error( "Error decompiling '{}' of container '{}'.", this.type.getMetadata( ).getFullName( ),
						this.type.getContainer( ).getName( ), err );
			}
			ErrorDialog.displayError( "Error decompiling type",
					"Error decompiling type: " + this.type.getMetadata( ).getFullName( ), err );
			
			return;
		}
		
		if( LOG.isInfoEnabled( ) ) {
			LOG.info( "Received type html for '{}' of container '{}'.", this.type.getMetadata( ).getFullName( ),
					this.type.getContainer( ).getName( ) );
		}
		this.decompiledContent = content;
		this.isDecompiled.setValue( true );
	}
	
}
