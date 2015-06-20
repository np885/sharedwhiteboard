'use strict';

app.service('Events',[
function () {
    var service = {};
    service.LineEvent = function(boardElementId, xStart, yStart, xEnd, yEnd){
        this.eventType = 'LineEvent';
        this.boardElementId = boardElementId;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }

    service.FreeHandEvent = function(boardElementId, xStart, yStart, xEnd, yEnd){
        this.eventType = 'FreeHandEvent';
        this.boardElementId = boardElementId;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }
    service.RectangleEvent = function (boardElementId, x, y, w, h){
        this.eventType = 'RectangleEvent';
        this.boardElementId = boardElementId;
        this.xStart = x;
        this.yStart = y;
        this.width = w;
        this.height = h;
    }
    service.CircleEvent = function (boardElementId, centerX, centerY, r){
        this.eventType = 'CircleEvent';
        this.boardElementId = boardElementId;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = r;
    }
    service.TextEvent = function (boardElementId, x, y, text){
        this.eventType = 'TextEvent';
        this.boardElementId = boardElementId;
        this.y = y;
        this.x = x;
        this.text = text;
    }

    service.DrawFinishedEvent = function (drawType, boardElementId){
        this.eventType = 'DrawFinishEvent';
        this.drawType = drawType;
        this.boardElementId = boardElementId;
    }

    return service;
}]);