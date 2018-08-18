var app = require('express')();
var express = require('express');
var handlebars = require('express3-handlebars');
var server = require('http').Server(app);
var io = require('socket.io')(server);
var path = require('path');
require(path.join(__dirname, '/../public/routes'))(app);
require(path.join(__dirname, '/../public/app/components/game/engine'))(io)


var port = 80;


// Loading the file index.html displayed to the client
// var server = http.createServer(function(req, res) {
//     fs.readFile('./src/Login/index.html', 'utf-8', function(error, content) {
//         res.writeHead(200, {"Content-Type": "text/html"});
//         res.end(content);
//     });
// });

// Loading socket.io
console.log(__dirname);
app.use(express.static(path.join(__dirname, '/../public')));

server.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }

    //console.log(`server is listening on ${port}`)
})