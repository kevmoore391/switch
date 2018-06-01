var http = require('http');
var fs = require('fs');
const Player = require('./src/Classes/Player.js');
const Game = require('./src/Classes/Game');

var port = 3000;

var games = [];
var loggedIn = {};
var players = [];
// Loading the file index.html displayed to the client
var server = http.createServer(function(req, res) {
    fs.readFile('./src/Login/index.html', 'utf-8', function(error, content) {
        res.writeHead(200, {"Content-Type": "text/html"});
        res.end(content);
    });
});

// Loading socket.io
var io = require('socket.io').listen(server);

io.sockets.on('connection', function (client, username) {
    
   
    // The other clients are told that someone new has arrived
    client.send('You have just connected, Welcome!');
    // emit('message', username + ' has just connected!');

    // When a "message" is received (click on the button), it's logged in the console
    client.on('message', function (message) {
        // The username of the person who clicked is retrieved from the session variables
        console.log(client.username + ' is speaking to me! They\'re saying: ' + message);
    }); 

    client.on('login', function (username, password, callback) {
        //this is where you check the user against db. 
        //lets assume thats all good for now.

        var loggedInAlready = isUserLoggedIn(username);
        if (loggedInAlready["loggedIn"]) {
            client.id = loggedInAlready["sessionId"];
            var data = {"sessionId": client.sessionId, "success":1, "message": "you are already logged in " + username};
        } else {
            var newPlayer = new Player(username, username, client.id);
            players.push(newPlayer);
            var data = {"sessionId": client.id, "success":1,"message": "Hello there " + username};
        }
        callback(data);
        
    });

    client.on('logout', function (username, callback) {
        var deleted = false;
        var loggedInAlready = isUserLoggedIn(username);

        if (loggedInAlready["loggedIn"]){
            players.splice(loggedInAlready["index"], 1);
            deleted = true;
        }
        
        callback({"success":1, "message": "See you soon!"});
        
    });

    client.on('getPlayers', function (username, callback) {
        var loggedInAlready = isUserLoggedIn(username);
        console.log(loggedInAlready);
        if(loggedInAlready["loggedIn"]){
            callback(players);
        } else {
            callback("Not logged in")
        }
        
        
    });

    client.on('findGame', function (username, callback) {
        var loggedInAlready = isUserLoggedIn(username);
        if(loggedInAlready["loggedIn"]){
            callback(games);
        } else {
            callback("Not logged in")
        }

    });

    client.on('findGame', function (username, callback) {
        var loggedInAlready = isUserLoggedIn(username);
        if(loggedInAlready["loggedIn"]){
            callback(games);
        } else {
            callback("Not logged in")
        }

    });

    client.on('joinGame', function (username, gameName) {
        client.join(gameName);
        client.in('game').emit('message', username + 'has joined the game');
    });
});

function isUserLoggedIn(username){
    var userInfo = {"loggedIn": false, "sessionId": null};
    for (var i in players){
        if (players[i].id == username){
            userInfo["loggedIn"] = true;
            userInfo["sessionId"] = players[i].sessionId;
            userInfo["index"] = players.indexOf(players[i]);
        }
    }

    return userInfo;
}


server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }

    console.log(`server is listening on ${port}`)
})