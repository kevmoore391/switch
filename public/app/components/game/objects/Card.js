function Card (selectCardValue, selectCardSuit) {
    //declaring the necessary variables
	
    this.joker = false;
    this.nameSuit = [ "Clubs", "Diamonds", "Hearts","Spades", "Black", "Red" ];
	this.nameSymbol = [ "\u2663", "\u2666", "\u2665", "\u2660", "\u25A0"];
    this.nameValue = [ "Ace", "2", "3", "4", "5", "6","7", "8", "9", "10", "Jack", "Queen", "King", "Joker" ];
    
    this.value = selectCardValue;
    this.suit = selectCardSuit;
    this.symbol = selectCardSuit;
    this.face = this.nameValue[this.value] + this.nameSuit[this.suit];
    
    this.evaluate = function(){
        if (this.value == 13) {
            
            this.symbol = 4;
            this.suit = 4;
            this.joker = true;
            
        }
    }
    this.setSuit = function(newSuit){
		this.suit = newSuit;
    }
    this.setValue = function(newValue){
		this.value = newValue;
	}
	
	this.setSymbol = function(newSymbol){
		this.symbol = newSymbol;
    }
    this.getSuit = function() {
		return this.suit;
	}
	
	this.getSuitString = function(){
		return this.nameSuit[this.suit];
	}
	
	this.getValueString = function(){
		return this.nameValue[this.value];
	}

	this.getValue = function() {
		return this.value;
	}

	this.getSymbol = function() {
		return this.symbol;
    }
    this.getUnicodeValueAndSuit = function() {
		var unicodeValueAndSuit = this.nameValue[this.value] + this.nameSymbol[this.suit];
		return unicodeValueAndSuit;
	}
    this.evaluate();
}

module.exports = Card;