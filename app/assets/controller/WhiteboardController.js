'use strict';

app.controller('WhiteboardController', ['$scope', '$routeParams', 'WhiteboardSocketService', 'constant', 'DrawService',
function($scope, $routeParams, WhiteboardSocketService, constant, DrawService){
    $scope.whiteboardname = WhiteboardSocketService.getWhiteboard().name;
    $scope.tools = [
        constant.DRAWTOOLS.FREEHAND,
        constant.DRAWTOOLS.CIRCLE,
        constant.DRAWTOOLS.RECTANGLE,
        constant.DRAWTOOLS.LINE,
        constant.DRAWTOOLS.TEXT,
        constant.DRAWTOOLS.MOVE
    ];
    $scope.tooling = $scope.tools[0];

    $scope.downloadLink = '';

    $scope.saveCanvas = function(){
        $scope.downloadLink = DrawService.prepareSaveCanvas();
    };

    $scope.selectTool = function(tool){
        $scope.tooling = tool;
    };

    WhiteboardSocketService.openSocketConnection();
}]);