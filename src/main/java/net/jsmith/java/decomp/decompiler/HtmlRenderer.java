package net.jsmith.java.decomp.decompiler;

import java.io.IOException;
import java.util.Objects;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

public abstract class HtmlRenderer extends HtmlAttributesFactory {

	private final HtmlCanvas canvas;
	
	protected HtmlRenderer( HtmlCanvas canvas ) {
		this.canvas = Objects.requireNonNull( canvas, "canvas" );
	}
	
	public String getHtml( ) {
		return this.canvas.toHtml( );
	}
	
	protected final void render( Renderer renderer ) {
		try {
			renderer.render( this.canvas );
		}
		catch( IOException ioe ) {
			throw new RuntimeException( ioe );
		}
	}
	
	public static interface Renderer {
		
		void render( HtmlCanvas canvas ) throws IOException;
		
	}
	
}
