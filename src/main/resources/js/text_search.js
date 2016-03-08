var search_state = {
	finder: undefined,
	matches: [ ],
	cur_match: 0,
	
	revert: function( ) {
		this.finder && this.finder.revert( )
		
		this.matches = []
		this.cur_match = 0
	},
	search: function( search_text ) {
		var self = this
		
		self.revert( )
		if( search_text ) {
			self.finder = findAndReplaceDOMText(
				document.body,
				{
					find: search_text,
					replace: function( portion, match ) {
						var s = document.createElement( "span" )
						s.className = "java_search"
						s.textContent = portion.text
						
						if( portion.index == 0 ) {
							self.matches.push( s )
						}
						return s
					}
				}
			)
			if( self.matches ) {
				self.matches[ 0 ].scrollIntoView( true )
			}
		}
	},
	next_match: function( ) {
		if( this.matches ) {
			this.cur_match = ( this.cur_match + 1 ) % this.matches.length
			this.matches[ this.cur_match ].scrollIntoView( true )
		}
	},
	prev_match: function( ) {
		if( this.matches ) {
			this.cur_match = ( this.cur_match - 1 )
			if( this.cur_match < 0 ) {
				this.cur_match = this.matches.length - 1
			}
			this.matches[ this.cur_match ].scrollIntoView( true )
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