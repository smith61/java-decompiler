package net.jsmith.java.decomp.document;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.fxmisc.richtext.StyleSpans;

public class Document {
	
	private final String text;
	
	private final StyleSpans< Collection< String > > styleSpans;
	
	private final List< Anchor > anchors;
	private final List< Reference > references;
	
	public Document( String text, StyleSpans< Collection< String > > styleSpans, List< Anchor > anchors, List< Reference > references ) {
		this.text = Objects.requireNonNull( text, "text" );
		
		this.styleSpans = Objects.requireNonNull( styleSpans, "styleSpans" );
		
		this.anchors = Objects.requireNonNull( anchors, "anchors" );
		this.references = Objects.requireNonNull( references, "references" );
	}

	public String getText( ) {
		return this.text;
	}

	public StyleSpans< Collection< String > > getStyleSpans( ) {
		return this.styleSpans;
	}

	public List< Anchor > getAnchors( ) {
		return Collections.unmodifiableList( this.anchors );
	}

	public List< Reference > getReferences( ) {
		return Collections.unmodifiableList( this.references );
	}
	
}
