'use strict';

app.directive('drawing',['WhiteboardSocketService', function(WhiteboardSocketService){
    return {
        restrict: 'A',
        link: function(scope, element, attrs, constant){
            var ctx = element[0].getContext('2d');
            var tool;

            scope.$watch(attrs.drawing, function(value) {
                tool = value;
            });

            // variable that decides if something should be drawn on mousemove
            var drawing = false;

            // the last coordinates before the current move
            var lastX;
            var lastY;

            function FreeHandEvent(boardElementId, xStart, yStart, xEnd, yEnd){
                this.eventType = "FreeHandEvent";
                this.boardElementId = boardElementId;
                this.xStart = xStart;
                this.yStart = yStart;
                this.xEnd = xEnd;
                this.yEnd = yEnd;
            }
            var freeHandLine = [];

            var currentX;
            var currentY;

            var drawLine = function(freeHandEvent){
                // line from
                ctx.moveTo(freeHandEvent.xStart, freeHandEvent.yStart);
                // to
                ctx.lineTo(freeHandEvent.xEnd, freeHandEvent.yEnd);
                // color
                ctx.strokeStyle = '#4bf';
                // draw it
                ctx.stroke();
            };

            WhiteboardSocketService.registerForSocketEvent('FreeHandEvent',drawLine);

            element.bind('mousedown', function(event){
                if(event.offsetX!==undefined){
                    lastX = event.offsetX;
                    lastY = event.offsetY;
                } else {
                    lastX = event.layerX - event.currentTarget.offsetLeft;
                    lastY = event.layerY - event.currentTarget.offsetTop;
                }

                // begins new line
                ctx.beginPath();

                drawing = true;
            });
            element.bind('mousemove', function(event){
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

                    var freeHandEvent = new FreeHandEvent(4711, lastX, lastY, currentX, currentY);
                    freeHandLine.push(freeHandEvent);

                    WhiteboardSocketService.send(JSON.stringify(freeHandEvent));

                    // set current coordinates to last one
                    lastX = currentX;
                    lastY = currentY;
                    console.log(tool);
                }

            });
            element.bind('mouseup', function(event){
                // stop drawing

                console.log(JSON.stringify(freeHandLine));
                drawing = false;
            });

            // canvas reset
            function reset(){
                element[0].width = element[0].width;
            }

        }
    };
}]);