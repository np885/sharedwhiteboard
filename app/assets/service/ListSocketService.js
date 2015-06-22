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

    service.openSocketConnection = function(errorCallback){
        if (connection != null && connection.readyState != 3) {
            console.log('connection already open.')
            //connection exists and is not closed.
            return;
        }

        //try to create a connection ticket:
        $http.post('/login/session/ticket')
            .success(function(data, status, headers, config) {
                //if we have a ticket, connect to it:
                connection = new WebSocket('ws://' + window.location.host + headers('Location'));

                connection.onopen = function () {
                };

                connection.onmessage = function (e) {
                    if (e.data === 'rejected') {
                        errorCallback();
                        return;
                    }
                    var event = JSON.parse(e.data);

                    console.log(event);

                    service.dispatchServerEvent(event);
                };

                // Log errors
                connection.onerror = function (error) {
                    console.log('WebSocket Error:');
                    console.log(error);
                };

            });
    };

    service.closeConnection = function(){
        if (connection != null) {
            connection.close();
        }
    };

    return service;
}]);