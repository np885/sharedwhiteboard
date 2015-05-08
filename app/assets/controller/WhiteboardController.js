'use strict';

app.controller('WhiteboardController', ['$scope', '$routeParams', 'WhiteboardSocketService',
function($scope, $routeParams, WhiteboardSocketService){
    $scope.boardId = $routeParams.boardId;

    WhiteboardSocketService.openSocketConnection();
}]);