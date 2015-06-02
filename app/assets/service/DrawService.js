'use strict';

app.service('DrawService',[ 'WhiteboardSocketService', 'DrawIdService',
function (WhiteboardSocketService, DrawIdService) {
    var drawLine;
    var beginPath;
    var tool;
    var service = {};
    var currentX;
    var currentY;
    // variable that decides if something should be drawn on mousemove
    var drawing = false;
    // the last coordinates before the current move
    var lastX;
    var lastY;
    function FreeHandEvent(boardElementId, xStart, yStart, xEnd, yEnd){
        this.eventType = "FreeHandEvent";
        this.boardElementId = boardElementId;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }
    var drawLineEvent = function(freeHandEvent){
        drawLine(freeHandEvent.xStart, freeHandEvent.yStart,
            freeHandEvent.xEnd, freeHandEvent.yEnd);
    };

    WhiteboardSocketService.registerForSocketEvent('FreeHandEvent',drawLineEvent);

    WhiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {

        initStateEvent.drawings.forEach(function (drawing) {
            DrawIdService.computeDrawing(drawing);
            if (drawing.type === 'FreeHandDrawing') {
                var xStart, yStart;
                drawing.points.forEach(function (point, i) {
                    if (i == 0) {
                        xStart = point.x;
                        yStart = point.y;
                    } else {
                        drawLine(xStart, yStart, point.x, point.y);
                        xStart = point.x;
                        yStart = point.y;
                    }
                });
            }// elseif type === '...'
        });
        DrawIdService.initId();
    });

    service.setDrawLine = function(fkt){
        drawLine = fkt;
    };
    service.setBeginPath = function(fkt){
        beginPath = fkt;
    };
    service.setTool = function(value){
        tool = value;
    };

    service.onMouseMove  = function(){
        if(drawing){
            // get current mouse position
            if(event.offsetX!==undefined){
                currentX = event.offsetX;
                currentY = event.offsetY;
            } else {
                currentX = event.layerX - event.currentTarget.offsetLeft;
                currentY = event.layerY - event.currentTarget.offsetTop;
            }
            var lastPoint = {};
            lastPoint.x = currentX;
            lastPoint.y = currentY;

            var freeHandEvent = new FreeHandEvent(DrawIdService.getCurrent(), lastX, lastY, currentX, currentY);

            WhiteboardSocketService.send(JSON.stringify(freeHandEvent));

            // set current coordinates to last one
            lastX = currentX;
            lastY = currentY;
            console.log(tool);
        }

    };
    service.onMouseDown = function(event){
        if(event.offsetX!==undefined){
            lastX = event.offsetX;
            lastY = event.offsetY;
        } else {
            lastX = event.layerX - event.currentTarget.offsetLeft;
            lastY = event.layerY - event.currentTarget.offsetTop;
        }
        // begins new line
        beginPath();

        drawing = true;
    };
    service.onMouseUp = function(event){
        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    };

    return service;
}]);