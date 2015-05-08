'use strict';

app.service('WhiteboardSocketService',[ '$http', function ($http) {
    var service = {};
    var websocketPath;

    service.setWebsocketPath = function(path){
        websocketPath = path;
    };

    service.openSocketConnection = function(){
        var connection = new WebSocket(websocketPath);

        connection.onopen = function () {
            connection.send('Ping'); // Send the message 'Ping' to the server
        };

        connection.onmessage = function (e) {
            console.log('Server: ' + e.data);
        };

        // Log errors
        connection.onerror = function (error) {
            console.log('WebSocket Error ' + error);
        };
        return connection;
    };



    return service;
}]);