function Player(username, name, session) {
    this.id = String(username);
    this.name = String(name);
    this.sessionId = session;
    this.currentGame = null;
    this.myHand = [];
    this.setName = function(name){
        this.name = name.charAt(0).toUpperCase() + name.slice(1);
    };
    this.inGame = function(playGame){
        this.currentGame = playGame;
    };
    this.sayHello = function(){

    };
    this.getHand = function(){
        
    };
    this.getCard = function(index) {
		return this.myHand[index];
    };
    this.removeCard = function(index) {
		
		this.myHand.splice(index, 1);
    };
    this.removeUserCard = function(card) {
		
		this.myHand.splice(this.myHand.indexOf(card), 1);
    };
    this.addCard = function(card) {
		
		this.myHand.push(card);
    };
    this.countHandCards = function() {
		return this.myHand.length;
	}
}

module.exports = Player;