function Player(username, name, session) {
    this.id = String(username);
    this.name = String(name);
    this.sessionId = session;
    this.inGame = false;
    this.setName = function(name){
        this._name = name.charAt(0).toUpperCase() + name.slice(1);
    };
    this.inGame = function(playGame){
        this.inGame = playGame;
    };
    this.sayHello = function(){

    };
}

module.exports = Player;