'use strict';

app.service('ListSocketService',[ '$http', function ($http) {
    //Event 'class'
    function SocketServerEvent(eventType) {
        this.eventType = eventType;
        this.callbacks = [];
    }
    SocketServerEvent.prototype.registerCallback = function(callback){
        this.callbacks.push(callback);
    };

    //List of hanlded Events:
    var handledServerEvents = {
        'ListStateChangedEvent' : new SocketServerEvent('ListStateChangedEvent'),
        'BoardUserOnlineEvent' : new SocketServerEvent('BoardUserOnlineEvent'),
        'BoardUserOfflineEvent' : new SocketServerEvent('BoardUserOfflineEvent')
    };

    var service = {};
    var connection;

    service.registerForSocketEvent = function(eventName, theCallback) {
        handledServerEvents[eventName].callbacks.push(theCallback);
    };

    service.dispatchServerEvent = function(e) {
        if (e.eventType != null) {
            //execute all callbacks that are registered for that type of server event:
            handledServerEvents[e.eventType].callbacks.forEach(function(registeredCallback) {
                registeredCallback(e);
            })
        }
    };

    service.openSocketConnection = function(){
        if (connection != null && connection.readyState != 3) {
            //connection exists and is not closed.
            return;
        }

        //try to create a connection ticket:
        $http.post('/login/session/ticket')
            .success(function(data, status, headers, config) {
                //if we have a ticket, connect to it:
                connection = new WebSocket('ws://' + window.location.host + headers('Location'));

                connection.onopen = function () {
                    //connection.send('Ping'); // Send the message 'Ping' to the server
                };

                connection.onmessage = function (e) {
                    var event = JSON.parse(e.data);

                    console.log(event);

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

    return service;
}]);