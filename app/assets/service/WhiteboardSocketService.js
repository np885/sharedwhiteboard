'use strict';

app.service('WhiteboardSocketService',[ '$http', function ($http) {
    var service = {};
    var websocketPath;
    var connection;
    var fkt;
    service.setWebsocketPath = function(path){
        websocketPath = path;
    };

    service.openSocketConnection = function(){
        connection = new WebSocket(websocketPath);

        connection.onopen = function () {
            //connection.send('Ping'); // Send the message 'Ping' to the server
        };

        connection.onmessage = function (e) {
            console.log(e.data);
            var e = JSON.parse(e.data);
            fkt(e.lastX, e.lastY, e.currentX, e.currentY);
        };

        // Log errors
        connection.onerror = function (error) {
            console.log('WebSocket Error ' + error);
        };
    };
    service.closeConnection = function(){
      connection.close();
    };

    service.send = function(payload){
      connection.send(payload);
    };

    service.setFkt = function(fktCallback){
        fkt = fktCallback;
    }



    return service;
}]);