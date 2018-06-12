function Game (name, limit, creator) {
    const TheDeck = require('./Deck.js');
    const TableCards = require('./TableCards.js');
    const CheckTrick = require('./CheckTrick.js');
    this.name = name;
    this.limit = limit;
    this.creator = creator;
    this.gamePlayers = [];
    this.playersTurn = null;
    this.gameOver = false;
    this.started = false;
    this.Deck = null;
    this.newDeck = new TheDeck();
    this.theTable = new TableCards();
    this.trickChecker = new CheckTrick();
    this.getName = function() {
        return this.name;
    };
    this.setName = function(name) {
        this.name = name;
    };
    this.getLimit = function() {
        return this.limit;
    };
    this.setLimit = function(limit) {
        this.limit = limit;
    };
    this.getPlayers = function() {
        return this.gamePlayers;
    };
    this.addPlayer = function(player) {
        this.gamePlayers.push(player);
    };
    this.leaveGame = function(player) {
        this.gamePlayers.splice(this.gamePlayers.indexOf(player), 1);
    };
    this.setWhosTurn = function(player) {
        this.playersTurn = username;
    };
    this.getWhosTurn = function() {
        return this.playersTurn;
    };
    this.setGameOver = function(allDone) {
        if(allDone){
            this.Deck = null;
            this.setWhosTurn(null);
            this.gamePlayers = [];
        }
        this.gameOver = allDone;
    }; 
    this.getGameOver = function() {
        return this.gameOver;
    };
    this.getStarted = function() {
        return this.started;
    };
    this.setStarted = function(started) {
        
        if (started){
            try{
                this.createDeck();
                this.dealCards();
                this.determineFirstPlayer();
            } catch(e){
                console.log(e);
                started = false;
            }
        }
        this.started = started;
    }; 
    this.createDeck = function() {
        this.Deck = this.newDeck.createNewDeck();
    };
    this.dealCards = function() {
        for (var playerCounter = 0; playerCounter < this.gamePlayers.length; playerCounter++) {
			var myHand = [];
			for (var card = 0; card < 7; card++) {
				var moveCard = this.Deck[0];
				this.Deck.splice(this.Deck.indexOf(moveCard), 1);
				myHand.push(moveCard);
			}
			this.gamePlayers[playerCounter].myHand = myHand;
		}
    };
    this.determineFirstPlayer = function(){
        this.playersTurn = this.gamePlayers[Math.floor(Math.random() * this.gamePlayers.length)];
    };
    this.determineFirstcard = function(){
        var nc = 0;
		var proceed = false;
		
		var firstCard = this.Deck[nc];
		
		do{
            if (trickChecker.CheckTrick(firstCard) == true){
                nc++;
                firstCard = this.Deck[nc];
            } else {
                proceed = true;
            }
		}while(proceed == false);
		
		
	    this.Deck.splice(nc, 1);
		this.theTable.addToTable(firstCard);
    };
    this.getCardInPlay = function(){
        return this.theTable.getTopTableCard();
    }
}

module.exports = Game;