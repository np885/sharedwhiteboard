'use strict';

app.directive('drawing',[ 'DrawService',
    function(DrawService){
    return {
        restrict: 'A',
        link: function(scope, element, attrs){
            var ctx = element[0].getContext('2d');
            ctx.font = '30px Arial';
            var h = attrs.height;
            var w = attrs.width;

            scope.$watch(attrs.drawing, function(value) {
                DrawService.setTool(value);
            });

            var paintBackgroundWhite = function(){
                ctx.rect(0, 0, w, h);
                ctx.fillStyle="#FFFFFF";
                ctx.fill();
            };
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
            var drawText = function(x, y, text, cursorPos, color) {
                if (typeof cursorPos !== 'undefined') {
                    text = [text.slice(0, cursorPos), '|', text.slice(cursorPos)].join('');
                }
                if (typeof color !== 'undefined') {
                    ctx.fillStyle = color;
                } else {
                    ctx.fillStyle = '#000000';
                }
                ctx.fillText(text, x, y);

            };

            var clear = function(background){
                ctx.clearRect(0, 0, w, h);
                if (typeof background!== 'undefined') {
                    paintBackgroundWhite();
                }
            };
            var mesureSize = function(text){
                  return ctx.measureText(text);
            };

            var getSaveUrl = function(){
                return element[0].toDataURL("image/png");
            };


            DrawService.setGetSaveUrl(getSaveUrl);
            DrawService.setDrawLine(drawLine);
            DrawService.setDrawText(drawText);
            DrawService.setMesureText(mesureSize);
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