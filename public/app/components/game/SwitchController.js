var switchApp = angular.module('SwitchApp', [])
switchApp.controller('SwitchController', ['$scope', function($scope) {
    var modal = $('#loginModal');
    var gameModal = $('#gameModal');
    var joinModal = $('#joinModal');
    $scope["showLoginModal"] = false;
    $scope.myHand = [];
    $scope.sessionId = null;
    $scope.username = '';
    $scope.password = '';
    $scope.myGame = '';
    $scope.selectedCards = [];
    $scope.availableGames = [];
    $scope.disableMoveButton = true;
    
    $scope.displayModal = function(){
        modal.css('display','block');
        return true;
    }

    $scope.displayCreateGameModal = function(){
        gameModal.css('display','block');
        return true;
    }

    $scope.displayJoinGameModal = function(){
        $scope.findGames();
        joinModal.css('display','block');
        return true;
    }

    $scope.closeModal = function(){
        modal.css('display','none');
        gameModal.css('display','none');
        joinModal.css('display','none');
        return true;
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            closeModal();
        }

        if (event.target == gameModal) {
            closeModal();
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------//

    
    // The visitor is asked for their username...

    var socket = io.connect('http://localhost:8080');
    //var login = io.connect('http://localhost:3000/login');
    //var socket = io.connect('http://localhost:3000', username);

    // A dialog box is displayed when the server sends us a "message"
    socket.on('message', function(message) {
        console.log(message);
    });

    $scope.login = function () {
        $scope.username = $('#username').val();
        $scope.password = $('#psw').val();

        $('#loginModal').css('display','none');

        socket.emit('login', $scope.username, $scope.password, function(data){
            console.log(data["message"]);
            if (data.success == 1){
                $scope.sessionId = data["sessionId"];
                $scope.loggedIn();
            } else {
                $scope.notLoggedIn();
            }
        });
    };

    $scope.createGame = function () {
        gameName = $('#gname').val();
        gameSize = $('#gsize').val();
        if (gameSize == '' || gameSize == 0 || gameSize == null) {
            alert("Please enter a correct game Size");
            return
        }
        $('#gameModal').css('display','none');

        socket.emit('createGame', $scope.username, gameSize, gameName, function(data){
            if ((data["gameCreated"]) && (data["gameJoined"])){
                $scope.gameOn();
                $scope.myGame = gameName;
            } else if (data["error"] === 1) {
                alert("A game with this name already exists");
            };

            if (data["error"] === 2){
                $scope.notLoggedIn();
            }
        });
    };

    $scope.joinGame = function () {
        var gameName = $('#jname').val();

        $('#joinModal').css('display','none');

        socket.emit('joinGame', $scope.username, gameName, function(data){
            
            if (data["gameJoined"]){
                $scope.gameOn();
                $scope.myGame = gameName;
            }

            if (data["error"] == 2){
                $scope.notLoggedIn();
            }
            if(data["start"]){
                $scope.startTheGame(gameName);
            }
        });
    };

    $scope.leaveGame = function () {
        $('#joinModal').css('display','none');
        socket.emit('leaveGame', $scope.username, function(data){
            console.log(data);
            if (data["gameLeft"]){
                $scope.leaveGame();
            }

            if (data["error"] == 2){
                $scope.notLoggedIn();
            }
        });
    };

    $scope.logout = function () {
        socket.emit('logout', $scope.username, function(data){
            console.log(data);
            
            if (data["success"]){
                $scope.notLoggedIn();
            }

            if (data["error"] == 2){
                $scope.notLoggedIn();
            }
        });
    };

    $scope.findGames = function(){
        socket.emit('findGame', $scope.username, function(data){
            if (data["error"] == 2){
                $scope.notLoggedIn();
                return;
            }
            
            $scope.availableGames = [];
            data.forEach(game => {
                $scope.availableGames.push(game);
            });
            console.log($scope.availableGames);
        });
    }

    $scope.startTheGame = function (gameName){
        socket.emit('StartTheGame', $scope.username, gameName);
    }

    socket.on('GameHasStarted', function(data) {
        data["MyHand"].forEach(card => {
            $scope.addCardToHand(card);
        });
        $scope.getGameInfo();
        console.log($scope.myHand);
        $scope.$apply();
    });

    socket.on('Yourturn', function(data) {
        if (data) {
            console.log("turn cards enabled");
            $scope.disableMoveButton = false;
        } else {
            $scope.disableMoveButton = true;
        }
        $scope.$apply();
    });

    $scope.chatWithGame = function () {
        socket.emit('communicatewithgame', $scope.username, $scope.myGame, function(data){
            console.log(data);
        });
    };

    $scope.getPlayers = function () {
        socket.emit('getPlayers', $scope.username, function(data){
            console.log("Players: ", data);
            if (data["error"] == 2){
                $scope.notLoggedIn();
            }
        });
    };

    $scope.selectCard = function (cardName, index) {
        cardName = cardName + index;
        console.log(cardName);
        if($scope.selectedCards.indexOf(String(cardName)) == -1){
            console.log("here");
            $('label[name='+ cardName +']').css("background", "red");
            $scope.selectedCards.push(cardName);
        } else {
            $('label[name='+ cardName +']').css("background", "white");
            $scope.selectedCards.splice($scope.selectedCards.indexOf(cardName), 1);
        }
    };

    $scope.playMove = function () {
        socket.emit('makeAMove', $scope.username, $scope.myGame, $scope.selectedCards, function(data){

            if (data["error"] == 1){
                console.log("your not in this game!!");
            } else if (data["error"] == 2){
                $scope.notLoggedIn();
            } else {
                $scope.removeCardsFromHand($scope.selectedCards);
            }
        });
    };
    $scope.removeCardsFromHand = function (cardsToRemove) {
        $scope.selectedCards.forEach(card => {
            $scope.myHand.splice($scope.myHand.indexOf(card), 1);
        })
    };

    $scope.addCardToHand = function(card){
        $scope.myHand.push(card);
    };

    socket.on('GameOver', function(data) {
        $scope.disableMoveButton = true;
    });

    $scope.getGameInfo = function () {
        socket.emit('getGameInfo', $scope.username, function(data){
            console.log(data);
            if (data["error"] == 2){
                $scope.notLoggedIn();
            }
        });
    };

    $scope.loggedIn = function(){
        $('#loginBtn').css('display','none');
        $('#logout').css('display','block');
        $('#findGames').css('display','block');
        $('#makeGame').css('display','block');
        $('#leaveGame').css('display','none');
        $('#joinGame').css('display', 'block');
        $('#makeAMove').css('display', 'none');
    }

    $scope.gameOn = function(){
        $('#loginBtn').css('display','none');
        $('#logout').css('display','none');
        $('#findGames').css('display','none');
        $('#makeGame').css('display','none');
        $('#leaveGame').css('display','block');
        $('#joinGame').css('display', 'none');
        $('#makeAMove').css('display', 'block');
    }

    $scope.leaveGame = function(){
        $('#loginBtn').css('display','none');
        $('#logout').css('display','block');
        $('#findGames').css('display','block');
        $('#makeGame').css('display','block');
        $('#leaveGame').css('display','none');
        $('#joinGame').css('display', 'block');
        $('#makeAMove').css('display', 'none');
        $scope.myGame = '';
    }

    $scope.notLoggedIn = function(){
        $('#loginBtn').css('display','block');
        $('#logout').css('display','none');
        $('#findGames').css('display','none');
        $('#makeGame').css('display','none');
        $('#leaveGame').css('display','none');
        $('#joinGame').css('display', 'none');
        $('#makeAMove').css('display', 'none');
    }
}]);