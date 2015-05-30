'use strict';

app.controller('WhiteboardController', ['$scope', '$routeParams', 'WhiteboardSocketService', 'constant',
function($scope, $routeParams, WhiteboardSocketService, constant){
    $scope.boardId = $routeParams.boardId;
    $scope.tools = [
        constant.DRAWTOOLS.FREEHAND,
        constant.DRAWTOOLS.CIRCLE,
        constant.DRAWTOOLS.RECTANGLE,
        constant.DRAWTOOLS.LINE,
        constant.DRAWTOOLS.TEXT];
    $scope.tooling = $scope.tools[0];

    $scope.selectTool = function(tool){
        $scope.tooling = tool;
    };

    WhiteboardSocketService.openSocketConnection();
}]);