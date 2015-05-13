'use strict';

app.directive('drawing',['WhiteboardSocketService', function(WhiteboardSocketService){
    return {
        restrict: 'A',
        link: function(scope, element){
            var ctx = element[0].getContext('2d');

            // variable that decides if something should be drawn on mousemove
            var drawing = false;

            // the last coordinates before the current move
            var lastX;
            var lastY;


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

                    var payload = {};
                    payload.eventType = "FreeHandEvent";
                    payload.xStart = lastX;
                    payload.yStart = lastY;
                    payload.xEnd = currentX;
                    payload.yEnd = currentY;
                    payload.boardElementId = 4711;//later useful

                    WhiteboardSocketService.send(JSON.stringify(payload));

                    // set current coordinates to last one
                    lastX = currentX;
                    lastY = currentY;
                }

            });
            element.bind('mouseup', function(event){
                // stop drawing
                drawing = false;
            });

            // canvas reset
            function reset(){
                element[0].width = element[0].width;
            }

        }
    };
}]);