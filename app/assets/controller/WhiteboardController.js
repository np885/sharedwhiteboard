app.controller('WhiteboardController', ['$scope', '$routeParams', function($scope, $routeParams){
    console.log('YIPIIE');
    $scope.boardId = $routeParams.boardId;
}]);