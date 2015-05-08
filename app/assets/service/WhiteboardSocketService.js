'use strict';

app.service('WhiteboardSocketService',[ '$http', function ($http) {
    var service = {};
    var websocketPath;
    var connection;
    service.setWebsocketPath = function(path){
        websocketPath = path;
    };

    service.openSocketConnection = function(){
        connection = new WebSocket(websocketPath);

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
    };
    service.closeConnection = function(){
      connection.close();
    };



    return service;
}]);