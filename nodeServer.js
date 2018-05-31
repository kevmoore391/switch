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
        
        var loggedInAlready = false;
        
        for (var thePlayer in players){
            if (thePlayer.sessionId = client.sessionId){
                loggedInAlready = true;
            }
        }

        if (loggedInAlready) {
            var data = {"sessionId": client.sessionId, "success":1, "message": "you are already logged in " + username};
        } else {
            var newPlayer = new Player(username, username, client.sessionId);
            players.push(newPlayer);
            var data = {"sessionId": client.sessionId, "success":1,"message": "Hello there " + username};
        }
        callback(data);
        
    });

    client.on('logout', function (username, callback) {
        var deleted = false;
        for (var player in players){
            if (player.id = username){
                players.splice(players.indexOf(player), 1);
                deleted = true;
            }
        }
        if (deleted){
            var data = {"success":1, "message": "See you soon!"};
        } else {
            var data = {"success":0, "message": "You were not logged in anyway ;)"};
        }
        
        callback(data);
        
    });

    client.on('getPlayers', function (callback) {
        
        callback(players);
        
    });

    client.on('findGame', function (me, username) {
        console.log("RIGHT");
        username("hi there");
        // if(games.length > 1){
        //     var listOfGames = games.toString;
        //     socket.emit('message', listOfGames);
        // } else {
        //     socket.emit('message', 'There are no Games right now! Why not Create one!');
        // }
    });

    client.on('joinGame', function (username, gameName) {
        client.join(gameName);
        client.in('game').emit('message', username + 'has joined the game');
    });
});


server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }

    console.log(`server is listening on ${port}`)
})