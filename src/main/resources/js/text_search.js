var search_state = {
	finder: undefined,
	matches: [ ],
	cur_match: 0,
	
	revert: function( ) {
		this.finder && this.finder.revert( )
		
		this.matches = []
		this.cur_match = -1
	},
	search: function( search_text ) {
		function escapeRegExp( s ) {
			return String( s ).replace( /([.*+?^=!:${}()|[\]\/\\])/g, '\\$1' );
		}
		
		var self = this
		
		self.revert( )
		if( search_text ) {
			var regex = RegExp( escapeRegExp( search_text ), 'gi' )
			
			var current_match = undefined
			self.finder = findAndReplaceDOMText(
				document.body,
				{
					find: regex,
					replace: function( portion, match ) {
						var s = document.createElement( "span" )
						s.className = "highlight"
						s.textContent = portion.text
						
						if( portion.index == 0 ) {
							current_match = [ ]
							self.matches.push( current_match )
						}
						current_match.push( s )
						return s
					}
				}
			)
			this.select( 0 )
		}
	},
	next_match: function( ) {
		this.select( this.cur_match + 1 )
	},
	prev_match: function( ) {
		this.select( this.cur_match - 1 )
	},
	
	select: function( match_index ) {
		if( this.matches.length > 0 ) {
			if( match_index >= this.matches.length ) {
				match_index = 0
			}
			if( match_index < 0 ) {
				match_index = this.matches.length - 1
			}
			this.matches[ match_index ][ 0 ].scrollIntoView( true )
			this.matches[ match_index ].forEach( function( e ) {
				e.className = "highlight_current"
			} )
			
			if( this.cur_match != -1 ) {
				this.matches[ this.cur_match ].forEach( function( e ) {
					e.className = "highlight"
				} )
			}
			this.cur_match = match_index
		}
	}
}
document.update_search = function( search_text ) {
	search_state.search( search_text )
}
document.next_match = function( ) {
	search_state.next_match( )
}
document.prev_match = function( ) {
	search_state.prev_match( )
}