class Player {

    constructor(username, name) {
        this.id = username;
        this.name = name
        this.gameId = null
    }

    set name(name) {
        this._name = name.charAt(0).toUpperCase() + name.slice(1);
    }
    get name() {
        return this._name;
    }

    get id() {
        return this.id;
    }

    get inGame() {
        return this.id;
    }

    set inGame(gameId) {
        this.gameId = gameId;
    }

    sayHello() {
        console.log('Hello, my name is ' + this.name + ', I have ID: ' + this.id);
    }

    
}