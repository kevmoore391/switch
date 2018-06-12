function Deck () {
    const Card = require('./Card.js');
    var nValues = 13; //number of Values
	var nSuits = 4; // number of suits
    var nCard = nValues * nSuits + 2; // number of cards in deck + jokers
    this.Deck = []
    
    this.shuffleDeck = function(array){
        var currentIndex = array.length, temporaryValue, randomIndex;

        // While there remain elements to shuffle...
        while (0 !== currentIndex) {
      
            // Pick a remaining element...
            randomIndex = Math.floor(Math.random() * currentIndex);
            currentIndex -= 1;
        
            // And swap it with the current element.
            temporaryValue = array[currentIndex];
            array[currentIndex] = array[randomIndex];
            array[randomIndex] = temporaryValue;
        }
        this.Deck = array;
    };

    this.createNewDeck = function(){
        for (var i = 0; i < 4; i++){
            for (var j = 0; j < 14; j++){
                var newCard = new Card(j, i);
	            this.Deck.push(newCard);
	        }
        }
	    
	    // Remove two of the jokers so deck contains only 2, so the deck will have 54 cards. 
	    this.Deck.splice(13, 1);
	    this.Deck.splice(26, 1);
	    this.Deck[39].setSuit(5);
	    this.shuffleDeck(this.Deck);
	    return this.Deck;
    };

    this.returnCard = function(pos){
   
      return this.Deck[pos];
    };
    
}

module.exports = Deck;