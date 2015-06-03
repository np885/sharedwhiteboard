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
                ctx.strokeStyle = '#4bf';
                // draw it
                ctx.stroke();
            };

            var clear = function(){
                ctx.clearRect(0, 0, element[0].width, element[0].height);
            };

            DrawService.setDrawLine(drawLine);
            DrawService.setClear(clear);
            DrawService.setBeginPath(function(){
                ctx.beginPath();
            });
            DrawService.setClosePath(function(){
                ctx.closePath();
            });

            element.bind('mousedown', DrawService.onMouseDown);
            element.bind('mousemove', DrawService.onMouseMove);
            element.bind('mouseup', DrawService.onMouseUp);
        }
    };
}]);