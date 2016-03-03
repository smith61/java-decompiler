( function( ) {
	var ret = { }
	ret.seekToAnchor = function( anchorID ) {
		var node = document.getElementById( anchorID )
		if( node === undefined || node === null ) {
			return false
		}
		
		node.scrollIntoView( true )
		return true
	}
	
	return ret;
} )( )