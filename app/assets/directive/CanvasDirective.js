'use strict';

app.directive('drawing',[ 'DrawService',
    function(DrawService){
    return {
        restrict: 'A',
        link: function(scope, element, attrs){
            var ctx = element[0].getContext('2d');

            scope.$watch(attrs.drawing, function(value) {
                DrawService.setTool(value);
            });

            var drawLine = function(xStart, yStart, xEnd, yEnd) {
                // line from
                ctx.moveTo(xStart, yStart);
                // to
                ctx.lineTo(xEnd, yEnd);
                // color
                // draw it
                //ctx.stroke();
            };

            var drawRectangle = function(x, y, w, h) {
                ctx.rect(x, y, w, h);
            };

            var drawCircle = function(centerX, centerY, radius) {
                ctx.rect(centerX-2, centerY, 5, 1);
                ctx.rect(centerX, centerY-2, 1, 5);
                ctx.moveTo(centerX+radius, centerY);
                ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI);
            };

            var clear = function(){
                ctx.clearRect(0, 0, element[0].width, element[0].height);
            };

            DrawService.setDrawLine(drawLine);
            DrawService.setDrawRectangle(drawRectangle);
            DrawService.setDrawCircle(drawCircle);
            DrawService.setClear(clear);
            DrawService.setBeginPath(function(pathColor){
                ctx.beginPath();
                if (typeof pathColor === 'undefined') {
                    ctx.strokeStyle = '#4bf'; //default color.
                } else {
                    ctx.strokeStyle = pathColor;
                }
            });
            DrawService.setClosePath(function(){
                //console.log("stroking..." + ctx.strokeStyle);
                ctx.stroke();
                ctx.closePath();
            });

            element.bind('mousedown', DrawService.onMouseDown);
            element.bind('mousemove', DrawService.onMouseMove);
            element.bind('mouseup', DrawService.onMouseUp);
        }
    };
}]);