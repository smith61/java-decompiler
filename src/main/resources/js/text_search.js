var instance;
document.update_search = function( search_text ) {
	instance && instance.revert( )
	instance = undefined
	
	if( search_text ) {
		var stencil = document.createElement( "span" )
		stencil.className = "java_search"
		instance = findAndReplaceDOMText( 
			document.body,
			{
				find: search_text,
				wrap: stencil
			}	
		)
	}
}