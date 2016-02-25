package net.jsmith.java.decomp.gui;

import java.util.Objects;

import javafx.scene.control.TextArea;
import net.jsmith.java.decomp.reference.TypeReference;

public class TypeReferenceView extends TextArea {

    private final TypeContainerView containerView;
    private final TypeReference typeReference;
    
    public TypeReferenceView( TypeContainerView containerView, TypeReference typeReference ) {
        super( "Loading..." );
        
        this.containerView = Objects.requireNonNull( containerView, "containerView" );
        this.typeReference = Objects.requireNonNull( typeReference, "typeReference" );
        
        this.typeReference.getTypeAST( ).whenCompleteAsync( ( ast, err ) -> {
            if( err != null ) {
                ErrorDialog.displayError( "Error loading AST.", "Error loading AST for type: " + typeReference.getFullyQualifiedName( ), err );
            }
            else {
                TypeReferenceView.this.setText( ast.get( ).getText( ) );
            }
        }, PlatformExecutor.INSTANCE );
    }
    
    public TypeContainerView getContainerView( ) {
        return this.containerView;
    }
    
    public TypeReference getTypeReference( ) {
        return this.typeReference;
    }
    
}
