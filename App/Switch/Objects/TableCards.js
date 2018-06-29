function TableCards () {
    this.tableCards = [];

    this.addToTable = function(addCard) {
		try {
			this.tableCards.unshift(addCard);
		} catch (e) {
			console.log("No card could be added to the table.");
		}
    };
    this.getTopTableCard = function() {
		return this.tableCards[0];
	}

	this.countCardsOnTable = function(){
		return this.tableCards.length;
	}

	this.getTableCard = function(){
		return this.tableCards[1];
	}
	
	this.getAllTableCards = function(){
		return this.tableCards;
	}
}

module.exports = TableCards;