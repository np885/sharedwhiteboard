'use strict';

app.service('MovementTooling',['AbstractTooling', 'BoardStateService', 'DrawIdService', 'WhiteboardSocketService', 'Events',
    function (abstractTooling, boardStateService, drawIdService, whiteboardSocketService, Events) {
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

            var requestRepaint = function() {
                whiteboardSocketService.sendEventPrivate(new Events.RepaintRequest());
            };

            var measureText;

            this.setMeasureText = function(fkt) {measureText = fkt};

            this.moving = false;

            this.mouseMove = function(event){
                if (this.moving) {
                    // get current mouse position
                    this.getCurrentMouse(event);

                    var deltaX =  this.currentX - this.startX;
                    var deltaY =  this.currentY - this.startY;

                    var socketEvent;
                    if (boardStateService.selectedDrawing.type === 'LineDrawing') {
                        socketEvent = new Events.LineEvent(
                            boardStateService.selectedDrawing.boardElementId,
                            boardStateService.selectedDrawing.points[0].x + deltaX,
                            boardStateService.selectedDrawing.points[0].y + deltaY,
                            boardStateService.selectedDrawing.points[1].x + deltaX,
                            boardStateService.selectedDrawing.points[1].y + deltaY
                        );
                        whiteboardSocketService.sendEventPrivate(socketEvent);
                    } else if (boardStateService.selectedDrawing.type === 'RectangleDrawing') {
                        socketEvent = new Events.RectangleEvent(
                            boardStateService.selectedDrawing.boardElementId,
                            boardStateService.selectedDrawing.x + deltaX,
                            boardStateService.selectedDrawing.y + deltaY,
                            boardStateService.selectedDrawing.width,
                            boardStateService.selectedDrawing.height
                        );
                        whiteboardSocketService.sendEventPrivate(socketEvent);
                    } else if (boardStateService.selectedDrawing.type === 'CircleDrawing') {
                        socketEvent = new Events.CircleEvent(
                            boardStateService.selectedDrawing.boardElementId,
                            boardStateService.selectedDrawing.centerX + deltaX,
                            boardStateService.selectedDrawing.centerY + deltaY,
                            boardStateService.selectedDrawing.radius
                        );
                        whiteboardSocketService.sendEventPrivate(socketEvent);
                    } else if (boardStateService.selectedDrawing.type === 'TextDrawing') {
                        socketEvent = new Events.TextEvent(
                            boardStateService.selectedDrawing.boardElementId,
                            boardStateService.selectedDrawing.x + deltaX,
                            boardStateService.selectedDrawing.y + deltaY,
                            boardStateService.selectedDrawing.text
                        );
                        whiteboardSocketService.sendEventPrivate(socketEvent);
                    }

                    this.startX = this.currentX;
                    this.startY = this.currentY;

                    if (socketEvent != null) {
                        this.sendMoveEvent(socketEvent);
                    }
                }
            };

            this.mouseUp = function(event){
                if (boardStateService.selectedDrawing !== null) {
                    var drawFinishedEvent  = new Events.DrawFinishedEvent('MoveEvent', boardStateService.selectedDrawing.boardElementId);
                    whiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
                }
                this.moving = false;
            };

            this.mouseDown = function(event){
                if (! this.moving) {
                    // get current mouse position
                    this.getCurrentMouse(event);

                    //select element:
                    boardStateService.selectedDrawing = null;
                    var dirtyHelper = this;
                    boardStateService.forAllDrawings(function(leDrawing) {
                        return dirtyHelper.isSelected(leDrawing, event);
                    }); //end for All Drawings.
                    //at this point, if drawing was found, selectedDrawing will be set. otherwise it will be null.
                    requestRepaint();
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
                        boardStateService.selectedDrawing = leDrawing;
                        this.moving = true;
                        this.getStartMouse(event);
                        requestRepaint();
                        return true;
                    }
                } else if (leDrawing.type === 'RectangleDrawing') {
                    var d = (event.isMobile) ? 10 : 5;//delta so that you dont have to hit exactly to the pixel.
                    if (inRect(this.currentX, this.currentY,
                            leDrawing.x - d, leDrawing.y - d, leDrawing.width + 2*d, leDrawing.height + 2*d)
                        && !inRect(this.currentX,this.currentY,
                            leDrawing.x + d,leDrawing.y + d, leDrawing.width - 2*d, leDrawing.height - 2*d)
                    ) {
                        boardStateService.selectedDrawing = leDrawing;
                        this.moving = true;
                        this.getStartMouse(event);
                        requestRepaint();
                        return true;
                    }
                } else if (leDrawing.type === 'CircleDrawing') {
                    var d = (event.isMobile) ? 8 : 4;//delta so that you dont have to hit exactly to the pixel.
                    if (inCircle(this.currentX, this.currentY,
                            leDrawing.centerX, leDrawing.centerY, leDrawing.radius + d)
                        && !inCircle(this.currentX, this.currentY,
                            leDrawing.centerX, leDrawing.centerY, leDrawing.radius - d)
                    ) {
                        boardStateService.selectedDrawing = leDrawing;
                        this.moving = true;
                        this.getStartMouse(event);
                        requestRepaint();
                        return true;
                    }
                } else if (leDrawing.type === 'TextDrawing') {
                    var h = 30;
                    if (inRect(this.currentX, this.currentY,
                            leDrawing.x, leDrawing.y-h, measureText(leDrawing.text).width, h)) {

                        boardStateService.selectedDrawing = leDrawing;
                        this.moving = true;
                        this.getStartMouse(event);
                        requestRepaint();
                        return true;
                    }
                }

                return false; //this drawing was not selected.
            };

        };
        MovementTooling.prototype = abstractTooling;
        return new MovementTooling();
    }]
);