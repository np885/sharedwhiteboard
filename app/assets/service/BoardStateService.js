'use strict';

app.service('BoardStateService',[
function () {
    var service = {};

    //draw elements state management:
    service.drawings = {};
    service.selectedDrawing = null;

    service.forAllDrawings = function(callback) {
        for(var boardElementId in service.drawings) {
            if (service.drawings.hasOwnProperty(boardElementId)) {
                var leDrawing = service.drawings[boardElementId];
                if (callback(leDrawing)) {
                    return; //callback returns true = found element, no need to iterate further.
                }
            }
        }
    };

    return service;
}]);