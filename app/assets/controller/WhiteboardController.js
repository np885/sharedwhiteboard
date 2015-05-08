'use strict';

app.controller('WhiteboardController', ['$scope', '$routeParams', function($scope, $routeParams){
    $scope.boardId = $routeParams.boardId;

    var connection = new WebSocket('ws://localhost:9000/whiteboards/' + $scope.boardId + '/session');

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
}]);