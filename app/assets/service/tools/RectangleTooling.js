'use strict';

app.service('RectangleTooling',['AbstractTooling', 'DrawIdService', 'WhiteboardSocketService', 'Events',
    function (abstractTooling, drawIdService, whiteboardSocketService, Events) {
        function RectangleTooling() {
            this.mouseMove = function(event){
                if (this.drawing) {
                    // get current mouse position
                    this.getCurrentMouse(event);
                    var rectWidth = this.currentX - this.startX;
                    var rectHeight = this.currentY - this.startY;
                    var rectangleEvent = new Events.RectangleEvent(
                        drawIdService.getCurrent(),
                        this.startX,
                        this.startY,
                        rectWidth,
                        rectHeight
                    );

                    whiteboardSocketService.sendEventPrivate(rectangleEvent);
                    this.sendMoveEvent(rectangleEvent);
                }
            };

            this.mouseUp = function(event){
                var drawFinishedEvent  = new Events.DrawFinishedEvent('RectangleEvent', drawIdService.getCurrent());
                whiteboardSocketService.send(JSON.stringify(drawFinishedEvent));

                // stop drawing
                drawIdService.incrementId();
                this.drawing = false;
            };

            this.mouseDown = function(event){
                if (!this.drawing) {
                    this.getStartMouse(event);
                    this.drawing = true;
                }
            };
        }
        RectangleTooling.prototype = abstractTooling;

        return new RectangleTooling();
    }]
);