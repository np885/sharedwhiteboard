'use strict';

app.controller('WhiteboardController', ['$scope', '$routeParams', 'WhiteboardSocketService', 'constant', 'DrawService',
function($scope, $routeParams, WhiteboardSocketService, constant, DrawService){
    $scope.whiteboardname = WhiteboardSocketService.getWhiteboard().name;
    function Tool(type, css) {
        this.type = type;
        this.css = css;
    }
    $scope.tools = [
        new Tool(constant.DRAWTOOLS.FREEHAND, 'freehand-tool'),
        new Tool(constant.DRAWTOOLS.LINE, 'line-tool'),
        new Tool(constant.DRAWTOOLS.RECTANGLE, 'rectangle-tool'),
        new Tool(constant.DRAWTOOLS.CIRCLE, 'circle-tool'),
        new Tool(constant.DRAWTOOLS.TEXT, 'glyphicon glyphicon-text-size'),
        new Tool(constant.DRAWTOOLS.MOVE, 'glyphicon glyphicon-move')
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