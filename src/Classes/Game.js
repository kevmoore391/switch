function Game (name, limit, creator) {
    const TheDeck = require('./Deck.js');
    this.name = name;
    this.limit = limit;
    this.creator = creator;
    this.gamePlayers = [];
    this.playersTurn = null;
    this.gameOver = false;
    this.started = false;
    this.Deck = null;
    this.newDeck = new TheDeck();
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
    this.getstarted = function() {
        return this.started;
    };
    this.setStarted = function(started) {
        this.started = started;
    }; 
    this.createDeck = function() {
        this.Deck = this.newDeck.createNewDeck();
    };
    this.dealCards = function() {
        

        for (var playerCounter = 0; playerCounter < this.gamePlayers.length; playerCounter++) {
			var myHand = [];
			for (var card = 0; card < 7; card++) {
				var moveCard = this.myDeck[0];
				this.myDeck.splice(this.myDeck.indexOf(moveCard), 1);
				myHand.push(moveCard);
			}
			this.amePlayers[playerCounter].myHand = myHand;
		}
    };
}

module.exports = Game;