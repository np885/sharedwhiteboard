'use strict';

app.service('DrawService',[ 'WhiteboardSocketService', 'Events', 'DrawIdService', 'constant', 'AbstractTooling', 'ToolSet',
function (WhiteboardSocketService, Events, DrawIdService, constant, abstractTooling, toolSet) {
    var service = {};

    //tool management:
    var tool;
    var selectedTooling = new FreehandTooling();

    //external methods (probably set by DrawDirective):
    var getSaveUrl;
    var drawLine;
    var drawText;
    var mesureText;
    var drawRectangle;
    var drawCircle;
    var clearCanvas;
    var beginPath;
    var closePath;

    //draw elements state management:
    var drawings = {};
    var selectedDrawing = null;


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

    service.drawRectangleEvent = function(rectEvent){
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
    };
    var drawTextEvent = function(textevent, cursorPosition){
        var drawing;
        if(drawings.hasOwnProperty(textevent.boardElementId)){
            //existingDrawing.
            drawing = drawings[textevent.boardElementId];
        } else {
            drawing = new Drawing('TextDrawing', textevent.boardElementId);
            drawings[drawing.boardElementId] = drawing;
        }
        if(typeof cursorPosition !== 'undefined') {
            if (selectedDrawing != null) {
                selectedDrawing.cursorPos = undefined;
            }
            drawing.cursorPos = cursorPosition;
            selectedDrawing = drawing;
        }
        drawing.x = textevent.x;
        drawing.y = textevent.y;
        drawing.text = textevent.text;
        repaint();
    };

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
        } else if(drawing.type === 'TextDrawing'){
            if(selectedDrawing === drawing){
                drawText(drawing.x, drawing.y, drawing.text, drawing.cursorPos, '#ff0000');
            } else {
                drawText(drawing.x, drawing.y, drawing.text);
            }
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
    WhiteboardSocketService.registerForSocketEvent('RectangleEvent', service.drawRectangleEvent);
    WhiteboardSocketService.registerForSocketEvent('CircleEvent', drawCircleEvent);
    WhiteboardSocketService.registerForSocketEvent('TextEvent', drawTextEvent);

    WhiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        drawings = {};
        initStateEvent.drawings.forEach(function (drawing) {
            DrawIdService.computeDrawing(drawing);
            drawings[drawing.boardElementId] = drawing;
        });
        DrawIdService.initId();
        repaint();
    });



    var repaint = function(background){
        //var start = new Date().getTime();

        clearCanvas(background);
        beginPath();
        for(var boardElementId in drawings){
            if(drawings.hasOwnProperty(boardElementId)){
                draw(drawings[boardElementId]);
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
    service.setMesureText = function(fkt){
        mesureText = fkt;
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



    service.forAllDrawings = function(callback) {
        for(var boardElementId in drawings) {
            if (drawings.hasOwnProperty(boardElementId)) {
                var leDrawing = drawings[boardElementId];
                if (callback(leDrawing)) {
                    return; //callback returns true = found element, no need to iterate further.
                }
            }
        }
    };

    function TextTooling() {
        this.mouseMove = function(event){/*Do Nothing*/};
        this.mouseUp = function(event){/*Do Nothing*/};

        this.mouseDown =  function(event){
            if(this.drawing) {
                //drawing is true if at least one letter was written.
                var drawFinishedEvent  = new DrawFinishedEvent('TextEvent', DrawIdService.getCurrent() - 1);
                WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
                this.drawing = false; //mouseDown = new id = new textelement = no letter written yet = drawing is false.
            }

            this.getCurrentMouse(event);

            var id = DrawIdService.getCurrent();
            DrawIdService.incrementId();
            var input = document.getElementById('drawText');
            //move input to click position to prevent "jumping" to hidden element on type:
            input.style['margin-left'] = this.currentX + 'px';
            input.style['margin-top'] = this.currentY-24 + 'px';
            //set focus without "jumping" to hidden element on click:
            var x = window.scrollX, y = window.scrollY;
            input.focus();
            window.scrollTo(x, y);

            input.value = '';
            var textEvent = new TextEvent(id, this.currentX, this.currentY, input.value);
            drawTextEvent(textEvent, input.selectionStart);
            var dirtyHelper = this;
            input.onkeyup = function (event) {
                dirtyHelper.drawing = true;
                var textEvent = new TextEvent(id, dirtyHelper.currentX, dirtyHelper.currentY, input.value);
                drawTextEvent(textEvent, input.selectionStart);
                WhiteboardSocketService.send(JSON.stringify(textEvent));
            };

        };
    };
    TextTooling.prototype = abstractTooling;


    function CircleTooling() {
        this.mouseDown = function(event){
            if (!this.drawing) {
                this.getStartMouse(event);
                this.drawing = true;
            }
        };

        this.mouseMove = function(event){
            if (this.drawing) {
                // get current mouse position
                this.getCurrentMouse(event);
                var dx = this.currentX - this.startX;
                var dy = this.currentY - this.startY;
                var radius = Math.sqrt(dx*dx + dy*dy);
                var circleEvent = new CircleEvent(DrawIdService.getCurrent(), this.startX, this.startY, radius);

                drawCircleEvent(circleEvent);
                this.sendMoveEvent(circleEvent);
            }
        };

        this.mouseUp = function(event){
            var drawFinishedEvent  = new DrawFinishedEvent('CircleEvent', DrawIdService.getCurrent());
            WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));

            // stop drawing
            DrawIdService.incrementId();
            this.drawing = false;
        };
    };
    CircleTooling.prototype = abstractTooling;


    function LineTooling() {
        this.mouseMove = function(event){
            if(this.drawing){
                // get current mouse position
                this.getCurrentMouse(event);
                var lineEvent = new LineEvent(
                    DrawIdService.getCurrent(),
                    this.startX,
                    this.startY,
                    this.currentX,
                    this.currentY);

                drawLineEvent(lineEvent);
                this.sendMoveEvent(lineEvent);
            }
        };

        this.mouseUp = function(event){
            var drawFinishedEvent  = new DrawFinishedEvent('LineEvent', DrawIdService.getCurrent());
            WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
            // stop drawing
            DrawIdService.incrementId();
            this.drawing = false;
        };

        this.mouseDown = function(event){
            if (! this.drawing) {
                this.getStartMouse(event);
                // begins new line
                this.drawing = true;
            }
        };
    };
    LineTooling.prototype = abstractTooling;


    function FreehandTooling() {
        this.mouseMove = function(event){

            if(this.drawing){
                // get current mouse position
                this.getCurrentMouse(event);

                var freeHandEvent = new FreeHandEvent(
                    DrawIdService.getCurrent(),
                    this.startX,
                    this.startY,
                    this.currentX,
                    this.currentY);

                drawFreeHandEvent(freeHandEvent);
                this.sendMoveEvent(freeHandEvent);

                // set current coordinates to last one
                this.startX = this.currentX;
                this.startY = this.currentY;
            }
        };

        this.mouseUp =  function(event){
            //Finished painting object
            var drawFinishedEvent  = new DrawFinishedEvent('FreeHandEvent', DrawIdService.getCurrent());
            WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
            // stop drawing
            DrawIdService.incrementId();
            this.drawing = false;
        };

        this.mouseDown = function(event){
            // begins new line
            this.getStartMouse(event);
            this.drawing = true;
        };
    };
    FreehandTooling.prototype = abstractTooling;

    function MovementTooling() {
        //returns whether the point (cx,cy) is inside the rect (x,y,w,h)
        var inRect = function(cx,cy,x,y,w,h) {
            //check in rect:
            return cx >= x && cx <= (x+w)   //proper horizontal area
                && cy >= y && cy <= y+h;     //proper vertical area
        };

        var inCircle = function(cx,cy, centerX, centerY, r) {
            var dx = centerX - cx;
            var dy = centerY - cy;
            return Math.sqrt(dx*dx + dy*dy) <= r;
        };

        this.moving = false;

        this.mouseMove = function(event){
            if (this.moving) {
                // get current mouse position
                this.getCurrentMouse(event);

                var deltaX =  this.currentX - this.startX;
                var deltaY =  this.currentY - this.startY;

                var socketEvent;
                if (selectedDrawing.type === 'LineDrawing') {
                    socketEvent = new LineEvent(
                        selectedDrawing.boardElementId,
                        selectedDrawing.points[0].x + deltaX,
                        selectedDrawing.points[0].y + deltaY,
                        selectedDrawing.points[1].x + deltaX,
                        selectedDrawing.points[1].y + deltaY
                    );
                    drawLineEvent(socketEvent);
                } else if (selectedDrawing.type === 'RectangleDrawing') {
                    socketEvent = new RectangleEvent(
                        selectedDrawing.boardElementId,
                        selectedDrawing.x + deltaX,
                        selectedDrawing.y + deltaY,
                        selectedDrawing.width,
                        selectedDrawing.height
                    );
                    service.drawRectangleEvent(socketEvent);
                } else if (selectedDrawing.type === 'CircleDrawing') {
                    socketEvent = new CircleEvent(
                        selectedDrawing.boardElementId,
                        selectedDrawing.centerX + deltaX,
                        selectedDrawing.centerY + deltaY,
                        selectedDrawing.radius
                    );
                    drawCircleEvent(socketEvent);
                } else if (selectedDrawing.type === 'TextDrawing') {
                    socketEvent = new TextEvent(
                        selectedDrawing.boardElementId,
                        selectedDrawing.x + deltaX,
                        selectedDrawing.y + deltaY,
                        selectedDrawing.text
                    );
                    drawTextEvent(socketEvent);
                }

                this.startX = this.currentX;
                this.startY = this.currentY;

                if (socketEvent != null) {
                    this.sendMoveEvent(socketEvent);
                }
            }
        };

        this.mouseUp = function(event){
            if (selectedDrawing !== null) {
                var drawFinishedEvent  = new DrawFinishedEvent('MoveEvent', selectedDrawing.boardElementId);
                WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
            }
            this.moving = false;
        };

        this.mouseDown = function(event){
            if (! this.moving) {
                // get current mouse position
                this.getCurrentMouse(event);

                //select element:
                selectedDrawing = null;
                var dirtyHelper = this;
                service.forAllDrawings(function(leDrawing) {
                    return dirtyHelper.isSelected(leDrawing, event);
                }); //end for All Drawings.
                //at this point, if drawing was found, selectedDrawing will be set. otherwise it will be null.
                repaint();
            }
        };

        this.isSelected = function(leDrawing, event) {
            if (leDrawing.type === 'LineDrawing') {
                //linear form: ((y2-y1)/(x2-x1))*( _x_ - x1)+y1 = _y_
                var x1 = leDrawing.points[0].x;
                var y1 = leDrawing.points[0].y;
                var x2 = leDrawing.points[1].x;
                var y2 = leDrawing.points[1].y;

                var d = (event.isMobile) ? 14 : 7; //delta so that you dont have to hit pixel perfect.

                //in range?
                var minX, maxX, minY, maxY;
                if (x1 >= x2) { minX = x2; maxX = x1; } else {minX = x1; maxX = x2}
                if (y1 >= y2) { minY = y2; maxY = y1; } else {minY = y1; maxY = y2}
                if (!inRect(this.currentX, this.currentY, minX-d, minY-d, maxX-minX + 2*d, maxY-minY + 2*d)) {
                    return false; //not in range, go to next element.
                }

                //on line? distance between point and line:
                // see http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
                var dist = Math.abs((y2-y1)*this.currentX - (x2-x1)*this.currentY + x2*y1-y2*x1)
                    / Math.sqrt((y2-y1)*(y2-y1) + (x2-x1)*(x2-x1));
                if (dist <= d) {
                    selectedDrawing = leDrawing;
                    this.moving = true;
                    this.getStartMouse(event);
                    repaint();
                    return true;
                }
            } else if (leDrawing.type === 'RectangleDrawing') {
                var d = (event.isMobile) ? 10 : 5;//delta so that you dont have to hit exactly to the pixel.
                if (inRect(this.currentX, this.currentY,
                        leDrawing.x - d, leDrawing.y - d, leDrawing.width + 2*d, leDrawing.height + 2*d)
                    && !inRect(this.currentX,this.currentY,
                        leDrawing.x + d,leDrawing.y + d, leDrawing.width - 2*d, leDrawing.height - 2*d)
                ) {
                    selectedDrawing = leDrawing;
                    this.moving = true;
                    this.getStartMouse(event);
                    repaint();
                    return true;
                }
            } else if (leDrawing.type === 'CircleDrawing') {
                var d = (event.isMobile) ? 8 : 4;//delta so that you dont have to hit exactly to the pixel.
                if (inCircle(this.currentX, this.currentY,
                        leDrawing.centerX, leDrawing.centerY, leDrawing.radius + d)
                    && !inCircle(this.currentX, this.currentY,
                        leDrawing.centerX, leDrawing.centerY, leDrawing.radius - d)
                ) {
                    selectedDrawing = leDrawing;
                    this.moving = true;
                    this.getStartMouse(event);
                    repaint();
                    return true;
                }
            } else if (leDrawing.type === 'TextDrawing') {
                var h = 30;
                if (inRect(this.currentX, this.currentY,
                        leDrawing.x, leDrawing.y-h, mesureText(leDrawing.text).width, h)) {

                    selectedDrawing = leDrawing;
                    this.moving = true;
                    this.getStartMouse(event);
                    repaint();
                    return true;
                }
            }

            return false; //this drawing was not selected.
        };

    };
    MovementTooling.prototype = abstractTooling;



    service.setTool = function(value){
        if(tool === constant.DRAWTOOLS.TEXT && selectedTooling.drawing){
            var drawFinishedEvent  = new DrawFinishedEvent('TextEvent', DrawIdService.getCurrent() - 1);
            WhiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
            if (selectedDrawing != null) {
                selectedDrawing.cursorPos = undefined;
            }
        }
        selectedDrawing = null;
        repaint();
        tool = value;
        switch(tool){
            case constant.DRAWTOOLS.FREEHAND:
                selectedTooling = new FreehandTooling();
                break;
            case constant.DRAWTOOLS.LINE:
                selectedTooling = new LineTooling();
                break;
            case constant.DRAWTOOLS.RECTANGLE:
                selectedTooling = toolSet.rectangleTooling;
                break;
            case constant.DRAWTOOLS.MOVE:
                selectedTooling = new MovementTooling();
                break;
            case constant.DRAWTOOLS.CIRCLE:
                selectedTooling = new CircleTooling();
                break;
            case constant.DRAWTOOLS.TEXT:
                selectedTooling = new TextTooling();
                break;
            default:
                selectedTooling = new FreehandTooling();
        }

        selectedTooling.drawing = false;
    };

    service.setGetSaveUrl = function(fkt){
        getSaveUrl = fkt;
    };

    service.prepareSaveCanvas = function(){
        selectedDrawing.cursorPos = undefined;
        selectedDrawing = null;
        repaint(true);
        return getSaveUrl();
    };


    //some UX for the Text-Tooling:
    document.getElementById('drawText').addEventListener("blur", function( event ) {
        if (selectedDrawing != null) {
            selectedDrawing.cursorPos = null;
        }
        selectedDrawing = null;
        repaint();
    }, true);




    return service;
}]);