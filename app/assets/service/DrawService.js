'use strict';

app.service('DrawService',[ 'WhiteboardSocketService', 'DrawIdService', 'constant',
function (WhiteboardSocketService, DrawIdService, constant) {
    var drawLine;
    var drawRectangle;
    var drawCircle;
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

    //Draw Events:
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
    function RectangleEvent(boardElementId, x, y, w, h){
        this.eventType = 'RectangleEvent';
        this.boardElementId = boardElementId;
        this.xStart = x;
        this.yStart = y;
        this.width = w;
        this.height = h;
    }
    function CircleEvent(boardElementId, centerX, centerY, r){
        this.eventType = 'CircleEvent';
        this.boardElementId = boardElementId;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = r;
    }

    function DrawFinishedEvent(drawType, boardElementId){
        this.eventType = 'DrawFinishEvent';
        this.drawType = drawType;
        this.boardElementId = boardElementId;
    }

    //Draw Objects:
    function Drawing(type, boardElementId){
        this.type = type;
        this.boardElementId = boardElementId;
    }

    //Event-Handler:

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

    var drawRectangleEvent = function(rectEvent){
        var drawing;
        if(drawings.hasOwnProperty(rectEvent.boardElementId)){
            //existingDrawing.
            drawing = drawings[rectEvent.boardElementId];
        } else {
            drawing = new Drawing('RectangleDrawing', rectEvent.boardElementId);
            drawings[drawing.boardElementId] = drawing;
        }
        drawing.x = rectEvent.xStart;
        drawing.y = rectEvent.yStart;
        drawing.width = rectEvent.width;
        drawing.height = rectEvent.height;

        repaint();
    }
    var drawCircleEvent = function(circleEvent){
        var drawing;
        if(drawings.hasOwnProperty(circleEvent.boardElementId)){
            //existingDrawing.
            drawing = drawings[circleEvent.boardElementId];
        } else {
            drawing = new Drawing('CircleDrawing', circleEvent.boardElementId);
            drawings[drawing.boardElementId] = drawing;
        }
        drawing.centerX = circleEvent.centerX;
        drawing.centerY = circleEvent.centerY;
        drawing.radius = circleEvent.radius;

        repaint();
    }

    //generic draw method, delegating to specific drawing methods:
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
        } else if(drawing.type === 'RectangleDrawing'){
            drawRectangle(drawing.x, drawing.y, drawing.width, drawing.height);
        } else if(drawing.type === 'CircleDrawing'){
            drawCircle(drawing.centerX, drawing.centerY, drawing.radius);
        }

        if (drawing === selectedDrawing) {
            closePath();
            beginPath();
        }
    };

    //
    // Event registration:
    //

    WhiteboardSocketService.registerForSocketEvent('FreeHandEvent', drawFreeHandEvent);
    WhiteboardSocketService.registerForSocketEvent('LineEvent', drawLineEvent);
    WhiteboardSocketService.registerForSocketEvent('RectangleEvent', drawRectangleEvent);
    WhiteboardSocketService.registerForSocketEvent('CircleEvent', drawCircleEvent);

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

    var getCurrentMouse = function(event) {
        if(event.offsetX !== undefined){
            currentX = event.offsetX;
            currentY = event.offsetY;
        } else {
            currentX = event.layerX - event.currentTarget.offsetLeft;
            currentY = event.layerY - event.currentTarget.offsetTop;
        }
    }
    var getStartMouse = function(event) {
        if(event.offsetX!==undefined){
            startX = event.offsetX;
            startY = event.offsetY;
        } else {
            startX = event.layerX - event.currentTarget.offsetLeft;
            startY = event.layerY - event.currentTarget.offsetTop;
        }
    }

    service.freeHandMouseMove = function(event){

        if(drawing){
            // get current mouse position
            getCurrentMouse(event);
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
        //Finished painting object
        var drawFinishedEvent  = new DrawFinishedEvent('FreeHandEvent', DrawIdService.getCurrent());
        WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    };
    service.onMouseUp = function(event) {
        return onMouseUpWrapper(event);
    };

    service.lineMouseUp = function(event){
        var drawFinishedEvent  = new DrawFinishedEvent('LineEvent', DrawIdService.getCurrent());
        WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    };
    service.lineMouseDown = function(event){
        if (!drawing) {
            getStartMouse(event);
            // begins new line
            drawing = true;
        }
    };
    service.lineMouseMove = function(event){
        if(drawing){
            event.stopPropagation();
            event.preventDefault();

            // get current mouse position
            getCurrentMouse(event);
            var freeHandEvent = new LineEvent(DrawIdService.getCurrent(), startX, startY, currentX, currentY);

            WhiteboardSocketService.send(JSON.stringify(freeHandEvent));
        }
    };


    //returns whether the point (cx,cy) is inside the rect (x,y,w,h)
    function inRect(cx,cy,x,y,w,h) {
        return cx >= x && cx <= (x+w)   //proper horizontal area
            && cy >= y && cy <= y+h     //proper vertical area
    }

    function inCircle(cx,cy, centerX, centerY, r) {
        var dx = centerX - cx;
        var dy = centerY - cy;
        return Math.sqrt(dx*dx + dy*dy) <= r;
    }

    var moving = false;
    var selectedDrawing = null;
    service.moveMouseDown= function(event){
        if (!moving) {
            event.stopPropagation();
            event.preventDefault();
            // get current mouse position
            getCurrentMouse(event);

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
                    } else if (leDrawing.type === 'RectangleDrawing') {
                        var d = 5;//delta so that you dont have to hit exactly to the pixel.
                        if (inRect(currentX, currentY,
                                leDrawing.x - d, leDrawing.y - d, leDrawing.width + 2*d, leDrawing.height + 2*d)
                            && !inRect(currentX,currentY,
                                leDrawing.x + d,leDrawing.y + d, leDrawing.width - 2*d, leDrawing.height - 2*d)
                        ) {
                            selectedDrawing = leDrawing;
                            moving = true;
                            startX = currentX;
                            startY = currentY;
                            repaint();
                            return;
                        }
                    } else if (leDrawing.type === 'CircleDrawing') {
                        var d = 4;//delta so that you dont have to hit exactly to the pixel.
                        if (inCircle(currentX, currentY,
                                leDrawing.centerX, leDrawing.centerY, leDrawing.radius + d)
                            && !inCircle(currentX, currentY,
                                leDrawing.centerX, leDrawing.centerY, leDrawing.radius - d)
                        ) {
                            selectedDrawing = leDrawing;
                            moving = true;
                            startX = currentX;
                            startY = currentY;
                            repaint();
                            return;
                        }
                    }
                }
            }
            //didnt found anything:
            selectedDrawing = null;
            repaint();
        }
    };


    service.moveMouseMove= function(event){
        if (moving) {
            event.stopPropagation();
            event.preventDefault();

            // get current mouse position
            getCurrentMouse(event);

            var deltaX =  currentX - startX;
            var deltaY =  currentY - startY;

            var socketEvent;
            if (selectedDrawing.type === 'LineDrawing') {
                socketEvent = new LineEvent(
                    selectedDrawing.boardElementId,
                    selectedDrawing.points[0].x + deltaX,
                    selectedDrawing.points[0].y + deltaY,
                    selectedDrawing.points[1].x + deltaX,
                    selectedDrawing.points[1].y + deltaY
                );
            } else if (selectedDrawing.type === 'RectangleDrawing') {
                socketEvent = new RectangleEvent(
                    selectedDrawing.boardElementId,
                    selectedDrawing.x + deltaX,
                    selectedDrawing.y + deltaY,
                    selectedDrawing.width,
                    selectedDrawing.height
                );
            } else if (selectedDrawing.type === 'CircleDrawing') {
                socketEvent = new CircleEvent(
                    selectedDrawing.boardElementId,
                    selectedDrawing.centerX + deltaX,
                    selectedDrawing.centerY + deltaY,
                    selectedDrawing.radius
                );
            }

            startX = currentX;
            startY = currentY;

            if (socketEvent != null) {
                WhiteboardSocketService.send(JSON.stringify(socketEvent));
            }
        }
    };
    service.moveMouseUp = function(event){
        if (selectedDrawing !== null) {
            var drawFinishedEvent  = new DrawFinishedEvent('MoveEvent', selectedDrawing.boardElementId);
            WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
        }
        moving = false;
    };

    service.rectMouseDown = function(event){
        if (!drawing) {
            getStartMouse(event);
            drawing = true;
        }
    }
    service.rectMouseMove = function(event){
        if (drawing) {
            event.stopPropagation();
            event.preventDefault();

            // get current mouse position
            getCurrentMouse(event);
            var rectWidth = currentX - startX;
            var rectHeight = currentY - startY;
            var rectangleEvent = new RectangleEvent(DrawIdService.getCurrent(), startX, startY, rectWidth, rectHeight);

            WhiteboardSocketService.send(JSON.stringify(rectangleEvent));
        }
    }
    service.rectMouseUp = function(event){
        var drawFinishedEvent  = new DrawFinishedEvent('RectangleEvent', DrawIdService.getCurrent());
        WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));

        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    }

    service.circleMouseDown = function(event){
        if (!drawing) {
            getStartMouse(event);
            drawing = true;
        }
    }
    service.circleMouseMove = function(event){
        if (drawing) {
            event.stopPropagation();
            event.preventDefault();

            // get current mouse position
            getCurrentMouse(event);
            var dx = currentX - startX;
            var dy = currentY - startY;
            var radius = Math.sqrt(dx*dx + dy*dy);
            var circleEvent = new CircleEvent(DrawIdService.getCurrent(), startX, startY, radius);

            WhiteboardSocketService.send(JSON.stringify(circleEvent));
        }
    }
    service.circleMouseUp = function(event){
        var drawFinishedEvent  = new DrawFinishedEvent('CircleEvent', DrawIdService.getCurrent());
        WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));

        // stop drawing
        DrawIdService.incrementId();
        drawing = false;
    }




    service.setDrawLine = function(fkt){
        drawLine = fkt;
    };
    service.setDrawRectangle = function(fkt){
        drawRectangle = fkt;
    };
    service.setDrawCircle = function(fkt){
        drawCircle = fkt;
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
            case constant.DRAWTOOLS.RECTANGLE:
                onMouseMoveWrapper = this.rectMouseMove;
                onMouseDownWrapper = this.rectMouseDown;
                onMouseUpWrapper = this.rectMouseUp;
                break;
            case constant.DRAWTOOLS.MOVE:
                onMouseMoveWrapper = this.moveMouseMove;
                onMouseDownWrapper = this.moveMouseDown;
                onMouseUpWrapper = this.moveMouseUp;
                break;
            case constant.DRAWTOOLS.CIRCLE:
                onMouseMoveWrapper = this.circleMouseMove;
                onMouseDownWrapper = this.circleMouseDown;
                onMouseUpWrapper = this.circleMouseUp;
                break;
            default:
                onMouseMoveWrapper = this.freeHandMouseMove;
                onMouseDownWrapper = this.freeHandMouseDown;
                onMouseUpWrapper = this.freeHandMouseUp;
        }
    };
    return service;
}]);