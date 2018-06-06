function Game (name, limit, creator) {
    this.name = name;
    this.limit = limit;
    this.creator = creator;
    this.gamePlayers = [];
    this.playersTurn = null;
    this.gameOver = false;
    this.started = false;
    this.getName = function() {
        return this.name;
    };
    this.getPlayers = function() {
        return this.players;
    };
    this.addPlayer = function(player) {
        this.gamePlayers.push(player);
    };
    this.leaveGame = function(player) {
        this.players.splice(this.players.indexOf(player), 1);
    };
    this.setWhosTurn = function(player) {
        this.playersTurn = username;
    };
    this.setGameOver = function(allDone) {
        this.gameOver = allDone;
    };    
}

module.exports = Game;