    module.exports = function(io) {
    
    var path = require('path');
    const Player = require(path.join(__dirname, '/objects/Player.js'));
    const Game = require(path.join(__dirname, '/objects/Game.js'));      

    var games = [];
    var loggedIn = {};
    var players = [];


    io.on('connection', function (client) {
        client.emit('message', "O_O");
        
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
                client.sessionId = loggedInAlready["sessionId"];
                var data = {"sessionId": client.sessionId, "success":1, "message": "you are already logged in " + username};
            } else {
                var newPlayer = new Player(username, username, client.id);
                players.push(newPlayer);
                var data = {"sessionId": client.sessionId, "success":1,"message": "Hello there " + username};
            }
            callback(data);
            
        });

        client.on('logout', function (username, callback) {
            var deleted = false;
            data = {};
            var loggedInAlready = isUserLoggedIn(username);
            if (loggedInAlready["loggedIn"]){
                deleted = logout(username, loggedInAlready["index"]);
                
            }
            if (deleted){
                data["status"] = "See you soon";
                data["error"] = 0;
            } else {
                data["status"] = "Uh Oh!, you aint goin nowhere!";
                data["error"] = 1;
            }
            data["success"] = deleted;
            callback(data);
            
        });

        client.on('getPlayers', function (username, callback) {
            var loggedInAlready = isUserLoggedIn(username);
            if(loggedInAlready["loggedIn"]){
                callback(players);
            } else {
                callback({"status":"Not logged in", "error": 2})
            }
            
        });

        client.on('findGame', function (username, callback) {
            var loggedInAlready = isUserLoggedIn(username);
            if(loggedInAlready["loggedIn"]){
                callback(games);
            } else {
                callback({"status":"Not logged in", "error": 2})
            }

        });

        client.on('createGame', function (username, size, gameName, callback) {
            var loggedInAlready = isUserLoggedIn(username);
            if(loggedInAlready["loggedIn"]){
                var data = {"gameJoined": false, "gameCreated": false, "error": 0};
                data["gameCreated"] = createGame(username, size, gameName);
                if (data["gameCreated"]){
                    client.join(gameName, function(){
                        io.sockets.to(gameName).emit(username + 'has joined the game');
                    });
                    
                    data["gameJoined"] = joinGame(username, gameName);
                    data["players"] = getGamePlayers(gameName);
                } else {
                    data["error"] = 1;
                }
                callback(data);
            } else {
                callback({"status":"Not logged in", "error": 2})
            }

        });

        client.on('joinGame', function (username, gameName, callback) {
            var data = {"gameJoined": false, "error": 0, "status":"all good", "start": false};
            var loggedInAlready = isUserLoggedIn(username);
            if(loggedInAlready["loggedIn"]){
                
                var success = joinGame(username, gameName);
                if(success){
                    var playersObject = getGamePlayers(gameName);
                    data["gameJoined"] = success;
                    data["players"] = playersObject;
                    client.join(gameName, function(){
                        io.sockets.in(gameName).emit('message', username + " has joined the game");
                    });
                }
                var canWeStart = canTheGameStart(gameName)
                data["start"] = canWeStart;
                callback(data);
            } else {
                data["error"] = 2;
                data["Not logged in"] = 2;
                callback(data);
            }
        });

        client.on('leaveGame', function (username, callback) {
            var data = {"gameLeft": false, "error": 0};
            var loggedInAlready = isUserLoggedIn(username);
            
            if(loggedInAlready["loggedIn"]){
                var thePlayer = findParticularPlayer(username);
                var theGame = findParticularGame(thePlayer.currentGame.name);
                if(theGame){
                    client.leave(thePlayer.currentGame);
                    client.in(thePlayer.currentGame).emit('message', username + 'has left the game');
                    leaveGame(username, thePlayer.currentGame);
                    
                    data["gameLeft"] = true;
                }
                
                callback(data);
            } else {
                data["status"] = "Not Logged In";
                data["error"] = 2;
                callback(data);
            }
        });

        client.on('getGameInfo', function (username, callback) {
            
            var loggedInAlready = isUserLoggedIn(username);
            if(loggedInAlready["loggedIn"]){
            
                var gameInfo = getCurrentGameInfo(username);
                
                callback(gameInfo);
            } else {
                callback({"status":"Not logged in", "error": 2})
            }
        });

        client.on('StartTheGame', function (username, game) {
            
            var loggedInAlready = isUserLoggedIn(username);
            if(loggedInAlready["loggedIn"]){
                var started = startTheGame(game);
                if(started){
                    
                    var thisGame = findParticularGame(game);
                    var participants = thisGame.getPlayers();
                    //io.sockets.in(game).emit('GameHasStarted', participants);
                    for(var player in participants){
                        //client.to(participants[player].sessionId).emit("message", "right " + participants[player].id + " lets go");
                        var data = {"players": getGamePlayers(game), "MyHand": participants[player].myHand, "WhosTurn":thisGame.getWhosTurn(), "StartingCard":thisGame.getCardInPlay()};
                        io.sockets.connected[participants[player].sessionId].emit("GameHasStarted", data);
                        
                    };
                    var playersTurn = thisGame.getWhosTurn();
                    io.sockets.connected[playersTurn.sessionId].emit("Yourturn", true);
                }
            }
            
        });

        client.on('MadeAMove', function (username, gameName, theMove, callback) {
            io.sockets.in(gameName).emit('message', username + " has jmade a move");
            console.log("The Move by: " + username + " in game:" + gameName + ", is: " + theMove);
            callback({error: 0});
        });

        client.on('amIInAGame', function (username, callback) {
            var alreadyInGame = isUserInGame(username);
            
            if (alreadyInGame) {
                var player = findParticularPlayer(username);
                var game = getCurrentGameInfo(username);
                data = {
                    "playerInfo": player, 
                    "game" : game,
                    "error" : 0
                }
                io.sockets.connected[participants[player].sessionId].emit("ThisIsTheGame", data);
                client.in(thePlayer.currentGame).emit('message', username + 'has rejoined the game.');
                callback(data);
            } else {
                callback({error: 1});
            };
        });

        client.on('checkForWinner', function (game) {
            var theGame = getCurrentGameInfo(game);
            
            if(theGame.started) {
                if (game.gamePlayers.length === 1){
                    player = game.gamePlayers[0];
                    io.sockets.connected[participants[player].sessionId].emit("YouWin", data);
                    client.in(theGame).emit('GameOver');
                } else if( game.gamePlayers.length > 1) {
                    game.gamePlayers.forEach(currentPlayer => {
                        if(currentPlayer.myHand.length === 0) {
                            io.sockets.connected[participants[currentPlayer].sessionId].emit("YouWin", data);
                            client.in(theGame).emit('GameOver');
                            return;
                        }
                    })
                }
            }
        });

    });

    function isUserLoggedIn(username){
        var userInfo = {"loggedIn": false, "sessionId": null};
        var thePlayer = findParticularPlayer(username);

        if (thePlayer) {
            userInfo["loggedIn"] = true;
            userInfo["sessionId"] = thePlayer.sessionId;
            userInfo["index"] = players.indexOf(thePlayer);
        }
        
        return userInfo;
    }

    function logout(username, index){
        var success = false;
        var thePlayer = findParticularPlayer(username);
        
        if(thePlayer.currentGame != null){
            leaveGame(username, thePlayer.currentGame);
        }

        players.splice(index, 1);

        var thePlayer = findParticularPlayer(username);
        if(!thePlayer){
            deleted = true;
        }
        return deleted;
    }

    function createGame(username, size, name){
        var success = false;
        var gameExistsAlready = false;
        try {
            var newGame = new Game(name, size, username);
            games.forEach(game => {
            if (game.name == newGame.name)
                gameExistsAlready = true;
                return;
            })
            if(!gameExistsAlready) {
                games.push(newGame);
                success = true;
            }
        } catch(e){
            console.log(e);
            success = false;
        }
        return success;
    }

    function deleteGame(name){
        var success = false
        var theGame = findParticularGame(name);
        if(theGame){
            try {
                games.splice(games.indexOf(theGame), 1);
                success = true;
            } catch(e){
                console.log(e);
                success = false;
            }
        }
        return success;
    }

    function leaveGame(username, name){
        
        var success = false
        var theGame = findParticularGame(name);
        var thePlayer = findParticularPlayer(username);
        if((theGame) && (thePlayer)){
            
            try {
                theGame.gamePlayers.splice(theGame.gamePlayers.indexOf(thePlayer), 1);
                thePlayer.currentGame = null
                success = true;
                var isTheGameEmpty = isGameEmpty(name);
                if(isTheGameEmpty){
                    deleteGame(name);
                }

            } catch(e){
                console.log(e);
                success = false;
            }
            
        } 
            
        return success;
    }

    function joinGame(username, name){
        var success = false;
        var theGame = findParticularGame(name);
        var thePlayer = findParticularPlayer(username);
        if((theGame) && (thePlayer)){
            if ((theGame.limit > theGame.gamePlayers.length) && (thePlayer.currentGame == null) && !(theGame.started)){
                theGame.addPlayer(thePlayer);
                thePlayer.inGame(theGame.name);
                success = true;
            }
        }
        return success;
    }


    function getGamePlayers(name){
        
        var playersInThisGame = [];
        var theGame = findParticularGame(name);
        if(theGame){
            for (var i in theGame.gamePlayers){
                playersInThisGame.push(theGame.gamePlayers[i].id);
            }
        }
        return playersInThisGame;
    }

    function findParticularGame(name){
        
        var theGame = null;
        
        for (var o in games){
            if (games[o].name == name){
                theGame = games[o];
            }
        }
        return theGame;
    }

    function findParticularPlayer(username){
        
        var thePlayer = null;
        for (var i in players){
            if (players[i].id == username){
                thePlayer = players[i];
            }
        }
        return thePlayer;
    }

    function getCurrentGameInfo(username){
        var theGame = {};
        var thePlayer = findParticularPlayer(username);
        if(thePlayer){
            theGame = findParticularGame(thePlayer.currentGame);
        }    
        
        return theGame;
    }

    function isUserInGame(username){
        var userInGame = true;
        var thePlayer = findParticularPlayer(username);
        if(thePlayer.currentGame == null){
            userInGame = false;
        }    
        
        return userInGame;
    }

    function isGameEmpty(game){
        var gameEmpty = false;
        var theGame = findParticularGame(game);
        if(theGame){
            var playersInGame = theGame.gamePlayers.length
            if(playersInGame ==0){
                gameEmpty = true;
            }
        }
        return gameEmpty;
    }

    function canTheGameStart(game){
        var start = false;
        var theGame = findParticularGame(game);
        if((theGame) && (theGame.started === false)){
            if(theGame.limit == theGame.gamePlayers.length){
                start = true;
            }

        }
        return start;
    }

    function startTheGame(game){
        var started = false;
        var goodToGo = canTheGameStart(game);
        if(goodToGo){
            var theGame = findParticularGame(game);
            theGame.setStarted(true);
            started = theGame.getStarted;
        }
        return started;
    }

}