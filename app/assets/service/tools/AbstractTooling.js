'use strict';

app.service('AbstractTooling',['WhiteboardSocketService',
    function (whiteboardSocketService) {

        function AbstractTooling() {
            this.drawing = false;
            this.startX;
            this.startY;
            this.currentX;
            this.currentY;
            this.getCurrentMouse = function(event) {
                if(event.offsetX !== undefined){
                    this.currentX = event.offsetX;
                    this.currentY = event.offsetY;
                } else {
                    this.currentX = event.layerX - event.currentTarget.offsetLeft;
                    this.currentY = event.layerY - event.currentTarget.offsetTop;
                }
            };
            this.getStartMouse = function(event) {
                if(event.offsetX!==undefined){
                    this.startX = event.offsetX;
                    this.startY = event.offsetY;
                } else {
                    this.startX = event.layerX - event.currentTarget.offsetLeft;
                    this.startY = event.layerY - event.currentTarget.offsetTop;
                }
            };

            var iter = 0;
            this.sendMoveEvent = function(e) {
                //ignore 2 of 3 events as Performance-Hack (will do for the demo)
                if (iter >= 2) {
                    iter = 0;
                    whiteboardSocketService.send(JSON.stringify(e));
                } else {
                    iter++;
                }
            };
        }
        return new AbstractTooling();
    }
]);