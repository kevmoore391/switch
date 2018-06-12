function Card (selectCardValue, selectCardSuit) {
    //declaring the necessary variables
	var suit;
	var value;
	var symbol;
    var joker;
    this.nameSuit = [ "Clubs", "Diamonds", "Hearts","Spades", "Black", "Red" ];
	this.nameSymbol = [ "\u2663", "\u2666", "\u2665", "\u2660", "\u25A0"];
    this.nameValue = [ "Ace", "2", "3", "4", "5", "6","7", "8", "9", "10", "Jack", "Queen", "King", "Joker" ];
    
    this.value = selectCardValue;
    this.suit = selectCardSuit;
    this.symbol = selectCardSuit;
    
    this.evaluate = function(){
        if (this.value == 13) {
            if (joker == false) {
                symbol = 4;
                suit = 4;
                joker = true;
            } else {
                symbol = 4;
                suit = 5;
            }
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