'use strict';

app.service('DrawService',[ 'WhiteboardSocketService', 'DrawIdService', 'constant',
function (WhiteboardSocketService, DrawIdService, constant) {
    var drawLine;
    var clearCanvas;
    var beginPath;
    var closePath;
    var tool;
    var service = {};
    var currentX;
    var currentY;
    // variable that decides if something should be drawn on mousemove
    var drawing = false;
    // the last coordinates before the current move
    var lastX;
    var lastY;
    var onMouseUpWrapper;
    var onMouseDownWrapper;
    var onMouseMoveWrapper;

    var drawings = {};


    var startX;
    var startY;
    function LineEvent(boardElementId, xStart, yStart, xEnd, yEnd){
        this.eventType = 'LineEvent';
        this.boardElementId = boardElementId;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }

    function FreeHandEvent(boardElementId, xStart, yStart, xEnd, yEnd){
        this.eventType = 'FreeHandEvent';
        this.boardElementId = boardElementId;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }
    function Drawing(type, boardElementId){
        this.type = type;
        this.boardElementId = boardElementId;
    }

    var drawFreeHandEvent = function(freeHandEvent){
        if(drawings.hasOwnProperty(freeHandEvent.boardElementId)){
            drawings[freeHandEvent.boardElementId].points.push({x : freeHandEvent.xEnd, y: freeHandEvent.yEnd});
        } else {
            var drawing = new Drawing('FreeHandDrawing', freeHandEvent.boardElementId);
            drawing.points = [];
            drawing.points.push({x : freeHandEvent.xStart, y: freeHandEvent.yStart});
            drawing.points.push({x : freeHandEvent.xEnd, y: freeHandEvent.yEnd});
            drawings[drawing.boardElementId] = drawing;
        }
        repaint();
    };
    var drawLineEvent = function(lineEvent){
        if(drawings.hasOwnProperty(lineEvent.boardElementId)){
            //update first point too, if it is a movement.
            drawings[lineEvent.boardElementId].points.pop();
            drawings[lineEvent.boardElementId].points.pop();
            drawings[lineEvent.boardElementId].points.push({x : lineEvent.xStart, y: lineEvent.yStart});
            drawings[lineEvent.boardElementId].points.push({x : lineEvent.xEnd, y: lineEvent.yEnd});
        } else {
            var drawing = new Drawing('LineDrawing', lineEvent.boardElementId);
            drawing.points = [];
            drawing.points.push({x : lineEvent.xStart, y: lineEvent.yStart});
            drawing.points.push({x : lineEvent.xEnd, y: lineEvent.yEnd});
            drawings[drawing.boardElementId] = drawing;
        }
        repaint();
    };

    var draw = function(drawing){
        if (drawing === selectedDrawing) {
            closePath();
            beginPath('#ff0000');
        }

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
        } else if(drawing.type === 'LineDrawing'){
            drawLine(drawing.points[0].x, drawing.points[0].y, drawing.points[1].x, drawing.points[1].y);
        }

        if (drawing === selectedDrawing) {
            closePath();
            beginPath();
        }
    };

    WhiteboardSocketService.registerForSocketEvent('FreeHandEvent', drawFreeHandEvent);
    WhiteboardSocketService.registerForSocketEvent('LineEvent', drawLineEvent);

    WhiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        drawings = {};
        initStateEvent.drawings.forEach(function (drawing) {
            DrawIdService.computeDrawing(drawing);
            drawings[drawing.boardElementId] = drawing;
        });
        DrawIdService.initId();
        repaint();
    });

    var repaint = function(){
        //var start = new Date().getTime();

        clearCanvas();
        beginPath();
        for(var boardElementId in drawings){
            if(drawings.hasOwnProperty(boardElementId)){
                draw(drawings[boardElementId]);
            }
        }
        closePath();

        //console.log( new Date().getTime() - start);
    };
    service.freeHandMouseMove = function(event){

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
        }

    };

    var iter = 0;
    service.onMouseMove = function(event){
        //ignore 3 of 4 events as Performance-Hack (will do for the demo)
        if (iter >= 3) {
            iter = 0;
            return onMouseMoveWrapper(event);
        } else {
            iter++;
        }
    };

    service.freeHandMouseDown = function(event){
        if(event.offsetX!==undefined){
            lastX = event.offsetX;
            lastY = event.offsetY;
        } else {
            lastX = event.layerX - event.currentTarget.offsetLeft;
            lastY = event.layerY - event.currentTarget.offsetTop;
        }
        // begins new line

        drawing = true;
    };
    service.onMouseDown = function(event) {
        return onMouseDownWrapper(event);
    };
    service.freeHandMouseUp = function(event){
        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    };
    service.onMouseUp = function(event) {
        return onMouseUpWrapper(event);
    };

    service.lineMouseUp = function(event){
        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    };
    service.lineMouseDown = function(event){
        if (!drawing) {
            if(event.offsetX!==undefined){
                startX = event.offsetX;
                startY = event.offsetY;
            } else {
                startX = event.layerX - event.currentTarget.offsetLeft;
                startY = event.layerY - event.currentTarget.offsetTop;
            }
            // begins new line

            drawing = true;

        }
    };
    service.lineMouseMove = function(event){
        if(drawing){
            event.stopPropagation();
            event.preventDefault();

            // get current mouse position
            if(event.offsetX!==undefined){
                currentX = event.offsetX;
                currentY = event.offsetY;
            } else {
                currentX = event.layerX - event.currentTarget.offsetLeft;
                currentY = event.layerY - event.currentTarget.offsetTop;
            }
            var freeHandEvent = new LineEvent(DrawIdService.getCurrent(), startX, startY, currentX, currentY);

            WhiteboardSocketService.send(JSON.stringify(freeHandEvent));
        }
    };

    var moving = false;
    var selectedDrawing = null;
    service.moveMouseDown= function(event){
        if (!moving) {
            event.stopPropagation();
            event.preventDefault();
            // get current mouse position
            if(event.offsetX!==undefined){
                currentX = event.offsetX;
                currentY = event.offsetY;
            } else {
                currentX = event.layerX - event.currentTarget.offsetLeft;
                currentY = event.layerY - event.currentTarget.offsetTop;
            }

            //select element:
            for(var boardElementId in drawings){
                if(drawings.hasOwnProperty(boardElementId)){

                    var leDrawing = drawings[boardElementId];
                    if (leDrawing.type === 'LineDrawing') {
                        //linear form: ((y2-y1)/(x2-x1))*( _x_ - x1)+y1 = _y_
                        var x1 = leDrawing.points[0].x;
                        var y1 = leDrawing.points[0].y;
                        var x2 = leDrawing.points[1].x;
                        var y2 = leDrawing.points[1].y;

                        //in range?
                        var minX, maxX, minY, maxY;
                        if (x1 >= x2) { minX = x2; maxX = x1; } else {minX = x1; maxX = x2}
                        if (y1 >= y2) { minY = y2; maxY = y1; } else {minY = y1; maxY = y2}
                        if (currentX <= (minX-2) || currentX >= (maxX+2)
                            || currentY <= (minY-2) || currentY >= (maxY+2)) {
                            continue;
                        }
                        //on line?
                        var y = Math.floor(((y2-y1)/(x2-x1))*( currentX - x1)+y1);

                        if (currentY <= (y+4) && currentY >= (y-4)) {
                            selectedDrawing = leDrawing;
                            //console.log("found drawing:");
                            //console.log(selectedDrawing);
                            moving = true;
                            startX = currentX;
                            startY = currentY;
                            repaint();
                            return;
                        }
                        //else {
                        //    console.log('y should be ' + y + ' but is ' + currentY);
                        //}
                    }
                }
            }
            //didnt found anything:
            selectedDrawing = null;
            repaint();
        }
    }
    service.moveMouseMove= function(event){
        if (moving) {
            event.stopPropagation();
            event.preventDefault();

            // get current mouse position
            if(event.offsetX!==undefined){
                currentX = event.offsetX;
                currentY = event.offsetY;
            } else {
                currentX = event.layerX - event.currentTarget.offsetLeft;
                currentY = event.layerY - event.currentTarget.offsetTop;
            }

            var deltaX =  currentX - startX;
            var deltaY =  currentY - startY;

            console.log(deltaX + ', ' + deltaY);

            var lineEvent = new LineEvent(
                selectedDrawing.boardElementId,
                selectedDrawing.points[0].x + deltaX,
                selectedDrawing.points[0].y + deltaY,
                selectedDrawing.points[1].x + deltaX,
                selectedDrawing.points[1].y + deltaY
            );

            startX = currentX;
            startY = currentY;

            WhiteboardSocketService.send(JSON.stringify(lineEvent));
        }
    }
    service.moveMouseUp = function(event){
        moving = false;
    }

    service.setDrawLine = function(fkt){
        drawLine = fkt;
    };
    service.setBeginPath = function(fkt){
        beginPath = fkt;
    };
    service.setClosePath = function(fkt){
        closePath = fkt;
    };
    service.setClear = function(fkt){
        clearCanvas = fkt;
    };





    service.setTool = function(value){
        tool = value;
        switch(tool){
            case constant.DRAWTOOLS.FREEHAND:
                onMouseMoveWrapper = this.freeHandMouseMove;
                onMouseDownWrapper = this.freeHandMouseDown;
                onMouseUpWrapper = this.freeHandMouseUp;
                break;
            case constant.DRAWTOOLS.LINE:
                onMouseMoveWrapper = this.lineMouseMove;
                onMouseDownWrapper = this.lineMouseDown;
                onMouseUpWrapper = this.lineMouseUp;
                break;
            case constant.DRAWTOOLS.MOVE:
                onMouseMoveWrapper = this.moveMouseMove;
                onMouseDownWrapper = this.moveMouseDown;
                onMouseUpWrapper = this.moveMouseUp;
                break;
            default:
                onMouseMoveWrapper = this.freeHandMouseMove;
                onMouseDownWrapper = this.freeHandMouseDown;
                onMouseUpWrapper = this.freeHandMouseUp;
        }
    };
    return service;
}]);