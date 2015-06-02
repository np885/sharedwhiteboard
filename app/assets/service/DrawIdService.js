'use strict';

app.service('DrawIdService',[ 'WhiteboardSocketService', 'AuthenticationService',
function (WhiteboardSocketService, AuthenticationService) {
    var nextBoardElementId = -1;
    var minBoardElementId = AuthenticationService.getUserId() * 10000;
    var maxBoardElementId = (AuthenticationService.getUserId() + 1) * 10000 - 1;
    var service = {};

    service.isInRange = function(drawing){
        return minBoardElementId <= drawing.boardElementId && drawing.boardElementId <= maxBoardElementId;
    };
    service.computeDrawing = function(drawing){
        if (this.isInRange(drawing)) {
            // element of this user:
            if (drawing.boardElementId > nextBoardElementId) {
                nextBoardElementId = drawing.boardElementId;
            }
        }
    };
    service.initId = function(){
        if (nextBoardElementId < 0) {
            //no elements by this user.
            nextBoardElementId = minBoardElementId;
        } else {
            //else: nextBoardElementId is the id of the newest object by the user
            // (next element must get one higher).
            this.incrementId();
        }
    };
    service.getCurrent = function(){
        return nextBoardElementId;
    };
    service.incrementId = function(){
        nextBoardElementId++;
    };
    return service;
}]);