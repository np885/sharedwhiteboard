'use strict';

app.controller('WhiteboardController', ['$scope', '$routeParams', 'WhiteboardSocketService',
function($scope, $routeParams, WhiteboardSocketService){
    $scope.boardId = $routeParams.boardId;

    var connection = WhiteboardSocketService.openSocketConnection();
}]);