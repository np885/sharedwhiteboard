'use strict';

app.service('WhiteboardSocketService',[ '$http', function ($http) {
    //Event 'class'
    function SocketServerEvent(eventType) {
        this.eventType = eventType;
        this.callbacks = [];
    }
    SocketServerEvent.prototype.registerCallback = function(callback){
        this.callbacks.push(callback);
    }

    //List of hanlded Events:
    var handledServerEvents = {
        'BoardUserOpenEvent' : new SocketServerEvent('BoardUserOpenEvent'),
        'InitialBoardStateEvent' : new SocketServerEvent('InitialBoardStateEvent')
    };

    var service = {};
    var websocketTicketPath;
    var connection;
    var drawFunction;

    service.setWebsocketPath = function(path){
        websocketTicketPath = path;
    };

    service.registerForSocketEvent = function(eventName, theCallback) {
        handledServerEvents[eventName].callbacks.push(theCallback);
    }

    service.dispatchServerEvent = function(e) {
        if (e.eventType != null) {
            //execute all callbacks that are registered for that type of server event:
            handledServerEvents[e.eventType].callbacks.forEach(function(registeredCallback) {
                registeredCallback(e);
            })
        } else {
            drawFunction(e.lastX, e.lastY, e.currentX, e.currentY);
        }
    }

    service.openSocketConnection = function(){
        //try to create a connection ticket:
        $http.post(websocketTicketPath)
            .success(function(data, status, headers, config) {
                //on success: connect to socket. Path of ticket will be send by server in Location Header.
                connection = new WebSocket(headers('Location'));

                connection.onopen = function () {
                    //connection.send('Ping'); // Send the message 'Ping' to the server
                };

                connection.onmessage = function (e) {
                    var event = JSON.parse(e.data);
                    service.dispatchServerEvent(event);
                };

                // Log errors
                connection.onerror = function (error) {
                    console.log('WebSocket Error ' + error);
                };
            });
    };

    service.closeConnection = function(){
      connection.close();
    };

    service.send = function(payload){
      connection.send(payload);
    };

    service.setFkt = function(fktCallback){
        drawFunction = fktCallback;
    }



    return service;
}]);