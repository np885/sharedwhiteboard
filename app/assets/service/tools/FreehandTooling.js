'use strict';

app.service('FreehandTooling',['AbstractTooling', 'DrawIdService', 'WhiteboardSocketService', 'Events',
    function (abstractTooling, drawIdService, whiteboardSocketService, Events) {
        function FreehandTooling() {
            this.mouseMove = function(event){

                if(this.drawing){
                    // get current mouse position
                    this.getCurrentMouse(event);

                    var freeHandEvent = new Events.FreeHandEvent(
                        drawIdService.getCurrent(),
                        this.startX,
                        this.startY,
                        this.currentX,
                        this.currentY);

                    whiteboardSocketService.sendEventPrivate(freeHandEvent);
                    this.sendMoveEvent(freeHandEvent);

                    // set current coordinates to last one
                    this.startX = this.currentX;
                    this.startY = this.currentY;
                }
            };

            this.mouseUp =  function(event){
                //Finished painting object
                var drawFinishedEvent  = new Events.DrawFinishedEvent('FreeHandEvent', drawIdService.getCurrent());
                whiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
                // stop drawing
                drawIdService.incrementId();
                this.drawing = false;
            };

            this.mouseDown = function(event){
                // begins new line
                this.getStartMouse(event);
                this.drawing = true;
            };
        };
        FreehandTooling.prototype = abstractTooling;

        return new FreehandTooling();
    }]
);