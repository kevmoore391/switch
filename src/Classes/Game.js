class Game {

    constructor(name, limit, creator) {
        this.name = name;
        this.limit = limit;
        this.creator = creator;
        this.players = [];
        this.playersTurn = null;
        this.gameOver = false;
      }

    set name(name) {
        this._name = name.charAt(0).toUpperCase() + name.slice(1);
    }
    get name() {
        return this._name;
    }
    
    get players() {
        return this.players;
    }

    set addPlayer(player) {
        this.players.push(player);
    }

    set whosTurn(username) {
        this.playersTurn = username;
    }

    get whosTurn() {
        return this.playersTurn;
    }
    
    get limit() {
        return this.limit;
    }
    
    get gameOver(){
        return this.gameOver
    }

    set gameOver(allDone) {
        this.gameOver = allDone;
    }

    
}