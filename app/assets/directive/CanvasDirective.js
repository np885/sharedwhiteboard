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

            DrawService.setDrawLine(drawLine);
            DrawService.setBeginPath(function(){
                ctx.beginPath();
            });

            element.bind('mousedown', DrawService.onMouseDown);
            element.bind('mousemove', DrawService.onMouseMove);
            element.bind('mouseup', DrawService.onMouseUp);
        }
    };
}]);