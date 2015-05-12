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
        //try to create ticket:
        $http.post(websocketPath).success(function(data, status, headers, config) {
            //on success: create socket. Path of ticket will be send by server in Location Header.
            connection = new WebSocket(headers('Location'));

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
        });;
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