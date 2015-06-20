'use strict';

app.service('LineTooling',['AbstractTooling', 'DrawIdService', 'WhiteboardSocketService', 'Events',
    function (abstractTooling, drawIdService, whiteboardSocketService, Events) {
        function LineTooling() {
            this.mouseMove = function(event){
                if(this.drawing){
                    // get current mouse position
                    this.getCurrentMouse(event);
                    var lineEvent = new Events.LineEvent(
                        drawIdService.getCurrent(),
                        this.startX,
                        this.startY,
                        this.currentX,
                        this.currentY);

                    whiteboardSocketService.sendEventPrivate(lineEvent);
                    this.sendMoveEvent(lineEvent);
                }
            };

            this.mouseUp = function(event){
                var drawFinishedEvent  = new Events.DrawFinishedEvent('LineEvent', drawIdService.getCurrent());
                whiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
                // stop drawing
                drawIdService.incrementId();
                this.drawing = false;
            };

            this.mouseDown = function(event){
                if (! this.drawing) {
                    this.getStartMouse(event);
                    // begins new line
                    this.drawing = true;
                }
            };
        }
        LineTooling.prototype = abstractTooling;

        return new LineTooling();
    }]
);