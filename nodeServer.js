var http = require('http');
var fs = require('fs');
var player = require('./src/Classes/Player');
var game = require('./src/Classes/Game');

var port = 3000;

var games = [];
var players = {}
// Loading the file index.html displayed to the client
var server = http.createServer(function(req, res) {
    fs.readFile('./src/Login/index.html', 'utf-8', function(error, content) {
        res.writeHead(200, {"Content-Type": "text/html"});
        res.end(content);
    });
});

// Loading socket.io
var io = require('socket.io').listen(server);

io.sockets.on('connection', function (socket, username) {
    
    // When the client connects, they are sent a message
    socket.emit('message', 'You are connected!');
    // The other clients are told that someone new has arrived
    socket.broadcast.emit('message', 'Another client has just connected!');

    // When a "message" is received (click on the button), it's logged in the console
    socket.on('message', function (message) {
        // The username of the person who clicked is retrieved from the session variables
        console.log(socket.username + ' is speaking to me! They\'re saying: ' + message);
    }); 

    socket.on('login', function (socket, username) {
        socket.username = username;
    });

    socket.on('findGame', function (socket, username) {
        if(games.length > 1){
            var listOfGames = games.toString;
            socket.emit('message', listOfGames);
        } else {
            socket.emit('message', 'There are no Games right now! Why not Create one!');
        }
    });

    socket.on('joinGame', function (socket, username, gameName) {
        socket.join(gameName);
        io.in('game').emit('message', username + 'has joined the game');
    });
});


server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }

    console.log(`server is listening on ${port}`)
})