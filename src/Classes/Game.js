function Game (name, limit, creator) {
    this.name = name.charAt(0).toUpperCase() + name.slice(1);
    this.limit = limit;
    this.creator = creator;
    this.players = [];
    this.playersTurn = null;
    this.gameOver = false;
    this.started = false;
    this.getName = function() {
        return this.name;
    };
    this.getPlayers = function() {
        return this.players;
    };
    this.addPlayers = function(player) {
        this.players.push(player);
    };
    this.leaveGame = function(player) {
        for (var i in this.players){
        if (this.players[i].id == player.id){
            this.players.splice(this.players.indexOf(this.players[i]), 1);
        }
    }
        this.players.remo(player);
    };
    this.setWhosTurn = function(player) {
        this.playersTurn = username;
    };
    this.setGameOver = function(allDone) {
        this.gameOver = allDone;
    };    
}

module.exports = Game;