function Player(username, name, session) {
    this.id = String(username);
    this.name = String(name);
    this.sessionId = session;
    this.currentGame = null;
    this.setName = function(name){
        this.name = name.charAt(0).toUpperCase() + name.slice(1);
    };
    this.inGame = function(playGame){
        this.currentGame = playGame;
    };
    this.sayHello = function(){

    };
}

module.exports = Player;