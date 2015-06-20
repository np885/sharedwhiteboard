'use strict';

app.service('DrawService',[ 'BoardStateService', 'WhiteboardSocketService', 'Events', 'DrawIdService', 'constant', 'AbstractTooling', 'ToolSet',
function (boardStateService, WhiteboardSocketService, Events, DrawIdService, constant, abstractTooling, toolSet) {
    var service = {};

    //tool management:
    var tool;
    var selectedTooling = toolSet.freehandTooling;

    //external methods (probably set by DrawDirective):
    var getSaveUrl;
    var drawLine;
    var drawText;
    var drawRectangle;
    var drawCircle;
    var clearCanvas;
    var beginPath;
    var closePath;


    //Draw Objects:
    function Drawing(type, boardElementId){
        this.type = type;
        this.boardElementId = boardElementId;
    }

    //Event-Handler:

    var drawFreeHandEvent = function(freeHandEvent){
        if(boardStateService.drawings.hasOwnProperty(freeHandEvent.boardElementId)){
            boardStateService.drawings[freeHandEvent.boardElementId].points.push({x : freeHandEvent.xEnd, y: freeHandEvent.yEnd});
        } else {
            var drawing = new Drawing('FreeHandDrawing', freeHandEvent.boardElementId);
            drawing.points = [];
            drawing.points.push({x : freeHandEvent.xStart, y: freeHandEvent.yStart});
            drawing.points.push({x : freeHandEvent.xEnd, y: freeHandEvent.yEnd});
            boardStateService.drawings[drawing.boardElementId] = drawing;
        }
        repaint();
    };
    var drawLineEvent = function(lineEvent){
        if(boardStateService.drawings.hasOwnProperty(lineEvent.boardElementId)){
            //update first point too, if it is a movement.
            boardStateService.drawings[lineEvent.boardElementId].points.pop();
            boardStateService.drawings[lineEvent.boardElementId].points.pop();
            boardStateService.drawings[lineEvent.boardElementId].points.push({x : lineEvent.xStart, y: lineEvent.yStart});
            boardStateService.drawings[lineEvent.boardElementId].points.push({x : lineEvent.xEnd, y: lineEvent.yEnd});
        } else {
            var drawing = new Drawing('LineDrawing', lineEvent.boardElementId);
            drawing.points = [];
            drawing.points.push({x : lineEvent.xStart, y: lineEvent.yStart});
            drawing.points.push({x : lineEvent.xEnd, y: lineEvent.yEnd});
            boardStateService.drawings[drawing.boardElementId] = drawing;
        }
        repaint();
    };

    service.drawRectangleEvent = function(rectEvent){
        var drawing;
        if(boardStateService.drawings.hasOwnProperty(rectEvent.boardElementId)){
            //existingDrawing.
            drawing = boardStateService.drawings[rectEvent.boardElementId];
        } else {
            drawing = new Drawing('RectangleDrawing', rectEvent.boardElementId);
            boardStateService.drawings[drawing.boardElementId] = drawing;
        }


        drawing.x = rectEvent.xStart;
        drawing.y = rectEvent.yStart;
        drawing.width = rectEvent.width;
        drawing.height = rectEvent.height;

        //normalize:
        if (drawing.width < 0) {
            drawing.width *= -1;
            drawing.x -= drawing.width;
        }
        if (drawing.height < 0) {
            drawing.height *= -1;
            drawing.y -= drawing.height;
        }

        repaint();
    };
    var drawCircleEvent = function(circleEvent){
        var drawing;
        if(boardStateService.drawings.hasOwnProperty(circleEvent.boardElementId)){
            //existingDrawing.
            drawing = boardStateService.drawings[circleEvent.boardElementId];
        } else {
            drawing = new Drawing('CircleDrawing', circleEvent.boardElementId);
            boardStateService.drawings[drawing.boardElementId] = drawing;
        }
        drawing.centerX = circleEvent.centerX;
        drawing.centerY = circleEvent.centerY;
        drawing.radius = circleEvent.radius;

        repaint();
    };
    var drawTextEvent = function(textevent){
        var drawing;
        if(boardStateService.drawings.hasOwnProperty(textevent.boardElementId)){
            //existingDrawing.
            drawing = boardStateService.drawings[textevent.boardElementId];
        } else {
            drawing = new Drawing('TextDrawing', textevent.boardElementId);
            boardStateService.drawings[drawing.boardElementId] = drawing;
        }
        if(typeof textevent.cursorPosition !== 'undefined') {
            if (boardStateService.selectedDrawing != null) {
                boardStateService.selectedDrawing.cursorPos = undefined;
            }
            drawing.cursorPos = textevent.cursorPosition;
            boardStateService.selectedDrawing = drawing;
        }
        drawing.x = textevent.x;
        drawing.y = textevent.y;
        drawing.text = textevent.text;
        repaint();
    };

    //generic draw method, delegating to specific drawing methods:
    var draw = function(drawing){
        if (drawing === boardStateService.selectedDrawing) {
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
        } else if(drawing.type === 'TextDrawing'){
            if(boardStateService.selectedDrawing === drawing){
                drawText(drawing.x, drawing.y, drawing.text, drawing.cursorPos, '#ff0000');
            } else {
                drawText(drawing.x, drawing.y, drawing.text);
            }
        }

        if (drawing === boardStateService.selectedDrawing) {
            closePath();
            beginPath();
        }
    };

    //
    // Event registration:
    //

    WhiteboardSocketService.registerForSocketEvent('FreeHandEvent', drawFreeHandEvent);
    WhiteboardSocketService.registerForSocketEvent('LineEvent', drawLineEvent);
    WhiteboardSocketService.registerForSocketEvent('RectangleEvent', service.drawRectangleEvent);
    WhiteboardSocketService.registerForSocketEvent('CircleEvent', drawCircleEvent);
    WhiteboardSocketService.registerForSocketEvent('TextEvent', drawTextEvent);
    WhiteboardSocketService.registerForSocketEvent('RequestRepaint', function(event) {repaint();});

    WhiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        boardStateService.drawings = {};
        initStateEvent.drawings.forEach(function (drawing) {
            DrawIdService.computeDrawing(drawing);
            boardStateService.drawings[drawing.boardElementId] = drawing;
        });
        DrawIdService.initId();
        repaint();
    });



    var repaint = function(background){
        //var start = new Date().getTime();

        clearCanvas(background);
        beginPath();
        for(var boardElementId in boardStateService.drawings){
            if(boardStateService.drawings.hasOwnProperty(boardElementId)){
                draw(boardStateService.drawings[boardElementId]);
            }
        }
        closePath();

        //console.log( new Date().getTime() - start);
    };

    service.onMouseDown = function(event) {
        event.stopPropagation();
        event.preventDefault();

        return selectedTooling.mouseDown(event);
    };
    service.onMouseUp = function(event) {
        event.stopPropagation();
        event.preventDefault();
        return selectedTooling.mouseUp(event);
    };
    service.onMouseMove = function(event){
        event.stopPropagation();
        event.preventDefault();
        return selectedTooling.mouseMove(event);
    };


    service.setDrawText = function(fkt){
        drawText = fkt;
    };

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

    service.setGetSaveUrl = function(fkt){
        getSaveUrl = fkt;
    };


    service.setTool = function(value){
        if(tool === constant.DRAWTOOLS.TEXT && selectedTooling.drawing){
            var drawFinishedEvent  = new Events.DrawFinishedEvent('TextEvent', DrawIdService.getCurrent() - 1);
            WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
            if (boardStateService.selectedDrawing != null) {
                boardStateService.selectedDrawing.cursorPos = undefined;
            }
        }
        boardStateService.selectedDrawing = null;
        repaint();
        tool = value;
        switch(tool){
            case constant.DRAWTOOLS.FREEHAND:
                selectedTooling = toolSet.freehandTooling;
                break;
            case constant.DRAWTOOLS.LINE:
                selectedTooling = toolSet.lineTooling;
                break;
            case constant.DRAWTOOLS.RECTANGLE:
                selectedTooling = toolSet.rectangleTooling;
                break;
            case constant.DRAWTOOLS.MOVE:
                selectedTooling = toolSet.movementTooling;
                break;
            case constant.DRAWTOOLS.CIRCLE:
                selectedTooling = toolSet.circleTooling;
                break;
            case constant.DRAWTOOLS.TEXT:
                selectedTooling = toolSet.textTooling;
                break;
            default:
                selectedTooling = toolSet.freehandTooling;
        }

        selectedTooling.drawing = false;
    };


    service.prepareSaveCanvas = function(){
        if (boardStateService.selectedDrawing != null) {
            boardStateService.selectedDrawing.cursorPos = undefined;
        }
        boardStateService.selectedDrawing = null;
        repaint(true);
        return getSaveUrl();
    };


    //some UX for the Text-Tooling:
    document.getElementById('drawText').addEventListener("blur", function( event ) {
        if (boardStateService.selectedDrawing != null) {
            boardStateService.selectedDrawing.cursorPos = undefined;
        }
        boardStateService.selectedDrawing = null;
        repaint();
    }, true);




    return service;
}]);