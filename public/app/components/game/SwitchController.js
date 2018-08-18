var switchApp = angular.module('SwitchApp', [])
switchApp.controller('SwitchController', function ($scope) {
    var modal = $('#loginModal');
    var gameModal = $('#gameModal');
    var joinModal = $('#joinModal');
    $scope.showLoginModal = false;

    $scope.displayModal = function(){
        console.log("hi");
        
        modal.css('display','block');
        return true;
    }

    $scope.displayCreateGameModal = function(){
        gameModal.css('display','block');
        return true;
    }

    $scope.displayJoinGameModal = function(){
        findGames();
        joinModal.css('display','block');
        return true;
    }

    $scope.closeModal = function(){
        modal.css('display','none');
        gameModal.css('display','none');
        joinModal.css('display','none');
        return true;
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == modal) {
            closeModal();
        }

        if (event.target == gameModal) {
            closeModal();
        }
        
    }

    //----------------------------------------------------------------------------------------------------------------------------//

    var sessionId = null;
    var username = '';
    var password = '';
    var myGame = '';
    var selectedCards = [];
    // The visitor is asked for their username...

    var socket = io.connect('http://localhost:80');
    //var login = io.connect('http://localhost:3000/login');
    //var socket = io.connect('http://localhost:3000', username);

    // A dialog box is displayed when the server sends us a "message"
    socket.on('message', function(message) {
        console.log(message);
    });


    $('#login').click(function () {
        username = $('#username').val();
        password = $('#psw').val();

        $('#loginModal').css('display','none');

        socket.emit('login', username, password, function(data){
            console.log(data["message"]);
            if (data.success == 1){
                sessionId = data["sessionId"];
                $('#loginBtn').css('display','none');
                $('#logout').css('display','block');
                $('#findGames').css('display','block');
                $('#makeGame').css('display','block');
                $('#leaveGame').css('display','none');
                $('#joinGame').css('display', 'block');
                $('#move').css('display', 'none');
                $('#com').css('display', 'none');
            } else {
                notLoggedIn();
            }
        });
    });

    $('#game').click(function () {
        gameName = $('#gname').val();
        gameSize = $('#gsize').val();
        if (gameSize == '' || gameSize == 0 || gameSize == null) {
            gameSize = 4;
        }
        $('#gameModal').css('display','none');

        socket.emit('createGame', username, gameSize, gameName, function(data){
            console.log(data);
            if ((data["gameCreated"]) && (data["gameJoined"])){
                
                $('#loginBtn').css('display','none');
                $('#logout').css('display','none');
                $('#findGames').css('display','none');
                $('#makeGame').css('display','none');
                $('#leaveGame').css('display','block');
                $('#joinGame').css('display', 'none');
                myGame = gameName;
            } else {
                $('#loginBtn').css('display','none');
                $('#logout').css('display','block');
                $('#findGames').css('display','block');
                $('#makeGame').css('display','block');
                $('#leaveGame').css('display','none');
                $('#joinGame').css('display', 'block');
                myGame = '';
            }

            if (data["error"] == 2){
                notLoggedIn();
            }
        });
    });

    $('#join').click(function () {
        var gameName = $('#jname').val();

        $('#joinModal').css('display','none');

        socket.emit('joinGame', username, gameName, function(data){
            
            if (data["gameJoined"]){
                
                $('#loginBtn').css('display','none');
                $('#logout').css('display','none');
                $('#findGames').css('display','none');
                $('#makeGame').css('display','none');
                $('#leaveGame').css('display','block');
                $('#joinGame').css('display', 'none');
                
                myGame = gameName;
            } else {
                $('#loginBtn').css('display','none');
                $('#logout').css('display','block');
                $('#findGames').css('display','block');
                $('#makeGame').css('display','block');
                $('#leaveGame').css('display','none');
                $('#joinGame').css('display', 'block');
                
                myGame = '';
            }

            if (data["error"] == 2){
                notLoggedIn();
            }
            if(data["start"]){
                startTheGame(gameName);
            }
        });
    });

    $('#leaveGame').click(function () {

        $('#joinModal').css('display','none');

        socket.emit('leaveGame', username, function(data){
            console.log(data);
            if (data["gameLeft"]){
                
                leaveGame();
            } else {
                $('#loginBtn').css('display','none');
                $('#logout').css('display','none');
                $('#findGames').css('display','none');
                $('#makeGame').css('display','none');
                $('#leaveGame').css('display','block');
                $('#joinGame').css('display', 'none');
                
            }

            if (data["error"] == 2){
                notLoggedIn();
            }
        });
    });

    $('#logout').click(function () {
        
        socket.emit('logout', username, function(data){
            console.log(data);
            
            if (data["success"]){
                $('#loginBtn').css('display','block');
                $('#logout').css('display','none');
                $('#findGames').css('display','none');
                $('#makeGame').css('display','none');
                $('#leaveGame').css('display','none');
                $('#joinGame').css('display', 'none');
                
            } else {
                console.log(data["status"]);
                $('#loginBtn').css('display','none');
                $('#logout').css('display','block');
                $('#findGames').css('display','block');
                $('#makeGame').css('display','block');
                $('#leaveGame').css('display','none');
                $('#joinGame').css('display', 'block');
                
            }

            if (data["error"] == 2){
                notLoggedIn();
            }
        });
    });

    socket.on('GameHasStarted', function(data) {
        
        console.log("your cards are");
        var hand = data["MyHand"]
        for(var i in hand){
            var value = hand[i].value;
            var suit = hand[i].suit;
            var card = hand[i].nameValue[value] + hand[i].nameSymbol[suit];

            $("#myCards").append('<label class="btn thiscard"  name="'+ card + '">');
            $("#myCards").append('<input type="checkbox" autocomplete="off" >');
            $("#myCards").append('<img src="http://localhost/images/Playing_card_diamond_6.svg" width="100" height="80" class="img-fluid img-thumbnail" alt="'+ card + '"/>');
            $("#myCards").append('</label>');
            
        }
    });



    socket.on('Yourturn', function(data) {
        
        if (data) {
            console.log("turn cards enabled");
        }
    });

    $('.move').click(function () {
        var move = 1;
        socket.emit('makeMyMove', username, myGame, move, function(data){

            if (data["error"] == 1){
                console.log("your not in this game!!");
            }
            if (data["error"] == 2){
                notLoggedIn();
            }
        });

    });

    socket.on('GameOver', function(data) {
        $('#move').css('display', 'block');
        $('#move').attr("disabled", "disabled");
        console.log(data);
    });

    $('#com').click(function () {
        socket.emit('communicatewithgame', username, myGame, function(data){

            console.log(data);
        });

    });



    $('#ReturnToLobby').click(function () {
        $('#loginBtn').css('display','none');
        $('#logout').css('display','block');
        $('#findGames').css('display','block');
        $('#makeGame').css('display','block');
        $('#leaveGame').css('display','none');
        $('#joinGame').css('display', 'block');

    });

    $('#gameInfo').click(function () {
        socket.emit('getGameInfo', username, function(data){
            console.log(data);
            if (data["error"] == 2){
                notLoggedIn();
            }
        });
    });

    $('#getPlayers').click(function () {
        socket.emit('getPlayers', username, function(data){
            console.log("Players: ", data);
            if (data["error"] == 2){
                notLoggedIn();
            }
        });
    });

    $('#findGames').click(function () {
        socket.emit('findGame', username, function(data){
            console.log(data);
            if (data["error"] == 2){
                notLoggedIn();
            }
        });
    });

    $('.thiscard').click(function () {
        var cardName = $(this).attr("name");
        
        if(selectedCards.indexOf(String(cardName)) == -1){
            
            $(this).css("background", "red");
            selectedCards.push(cardName);
        } else {
            $(this).css("background", "white");
            selectedCards.splice(selectedCards.indexOf(cardName), 1);
        }

    });

    $('#test').click(function () {
        
        $("#myCards").append('<label class="btn thiscard"  name="11" style="background: white;"><input type="checkbox" autocomplete="off"><img src="http://localhost/images/Playing_card_diamond_6.svg" width="100" height="80" class="img-fluid img-thumbnail" alt="22"/></label>');
        
    });

    function gameOn(){
        $('#loginBtn').css('display','none');
        $('#logout').css('display','none');
        $('#findGames').css('display','none');
        $('#makeGame').css('display','none');
        $('#leaveGame').css('display','block');
        $('#joinGame').css('display', 'none');
    }
    function leaveGame(){
        $('#loginBtn').css('display','none');
        $('#logout').css('display','block');
        $('#findGames').css('display','block');
        $('#makeGame').css('display','block');
        $('#leaveGame').css('display','none');
        $('#joinGame').css('display', 'block');
        myGame = '';
    }
    function notLoggedIn(){
        $('#loginBtn').css('display','block');
        $('#logout').css('display','none');
        $('#findGames').css('display','none');
        $('#makeGame').css('display','none');
        $('#leaveGame').css('display','none');
        $('#joinGame').css('display', 'none');
    }
    function findGames(){
        socket.emit('findGame', username, function(data){
            $('#gameList').html();
            for(i in data){
                $('#gameList').append('<p>' + data[i].name + '</p>');
            }
        });
    }

    function startTheGame(gameName){
        socket.emit('StartTheGame', username, gameName);
    }
});