'use strict';

app.service('CircleTooling',['AbstractTooling', 'DrawIdService', 'WhiteboardSocketService', 'Events',
    function (abstractTooling, drawIdService, whiteboardSocketService, Events) {
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
                    var circleEvent = new Events.CircleEvent(drawIdService.getCurrent(), this.startX, this.startY, radius);

                    whiteboardSocketService.sendEventPrivate(circleEvent);
                    this.sendMoveEvent(circleEvent);
                }
            };

            this.mouseUp = function(event){
                var drawFinishedEvent  = new Events.DrawFinishedEvent('CircleEvent', drawIdService.getCurrent());
                whiteboardSocketService.send(JSON.stringify(drawFinishedEvent));

                // stop drawing
                drawIdService.incrementId();
                this.drawing = false;
            };
        }
        CircleTooling.prototype = abstractTooling;

        return new CircleTooling();
    }]
);