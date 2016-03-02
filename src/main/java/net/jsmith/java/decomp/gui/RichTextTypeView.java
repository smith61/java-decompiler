package net.jsmith.java.decomp.gui;

import java.util.Objects;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.layout.BorderPane;
import net.jsmith.java.decomp.decompiler.DecompilerUtils;
import net.jsmith.java.decomp.utils.ThreadPools;
import net.jsmith.java.decomp.workspace.Type;

public class RichTextTypeView extends BorderPane {
	
	private static final Logger LOG = LoggerFactory.getLogger( RichTextTypeView.class );
	
	private final ContainerView containerView;
	private final Type type;
	
	private final CodeArea contentView;
	
	public RichTextTypeView( ContainerView containerView, Type type ) {
		this.containerView = Objects.requireNonNull( containerView, "containerView" );
		this.type = Objects.requireNonNull( type, "type" );
		
		
		this.contentView = new CodeArea( "Decompiling..." );
		this.contentView.getStylesheets( ).add( RichTextTypeView.class.getResource( "/css/type-rt.css" ).toExternalForm( ) );
		this.contentView.setParagraphGraphicFactory( LineNumberFactory.get( this.contentView ) );
		this.contentView.setEditable( false );
		this.contentView.setWrapText( false );
		
		this.setCenter( this.contentView );
		
		DecompilerUtils.defaultDecompileRT( type ).whenCompleteAsync( ( doc, err ) -> {
			if( err != null ) {
				if( LOG.isErrorEnabled( ) ) {
					LOG.error( "Error decomiling type '{}' from container '{}'.", type.getMetadata( ).getFullName( ), type.getContainer( ).getName( ), err );
				}
			}
			else {
				this.contentView.replaceText( doc.getText( ) );
				this.contentView.setStyleSpans( 0, doc.getStyleSpans( ) );
				this.contentView.moveTo( 0 );
			}
		}, ThreadPools.PLATFORM );
	}
	
	public ContainerView getContainerView( ) {
		return this.containerView;
	}
	
	public Type getType( ) {
		return this.type;
	}
	
}
