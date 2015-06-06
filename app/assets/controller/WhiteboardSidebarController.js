'use strict';

app.controller('WhiteboardSidebarController', ['$scope', 'WhiteboardSocketService',
function($scope, whiteboardSocketService){
    function Collaborator(id, name, join, online, owner) {
        this.id = id;
        this.name = name;
        //TODO: online != join. online -> logged-in in App. join -> logged-in Whiteboard.
        this.online = (typeof online != 'undefined') ? online : false;
        this.join = (typeof join != 'undefined') ? join : false;
        //TODO: Adding owner flag for special STAR ;-)
        this.owner = (typeof owner != 'undefined') ? owner : false;
        this.toString = function() {return "{" + this.id + "," + this.name + "}";}
    }
    function WhiteboardLog(id, name, typ){
        this.id = id;
        this.name = name;
        this.typ = typ;
        this.toString = function(){return "{" + this.id + ", " + this.name + ", " + this.typ + "}";}
    }

    $scope.collaborators = [];
    $scope.whiteboardlog = [];

    whiteboardSocketService.registerForDrawEvent(function(drawEvent){
        $scope.$apply(function(){
            $scope.whiteboardlog.unshift(new WhiteboardLog(drawEvent.boardElementId, "test", drawEvent.eventType));
        });
    });

    whiteboardSocketService.registerForSocketEvent('BoardUserOpenEvent', function(boardUserOpenEvent) {
        //-> user joining...
        $scope.$apply(function() {
            //need scope-apply cause we are out of angular digest cycle, when the server sends events and calls this callback
            var alreadyMember = false;
            $scope.collaborators.forEach(function(collab) {
                if (collab.id === boardUserOpenEvent.userId) {
                    collab.join = true;
                    alreadyMember = true;
                }
            });
            if (!alreadyMember) {
                $scope.collaborators.push(new Collaborator(boardUserOpenEvent.userId, boardUserOpenEvent.username, true, true, true));
            }
        });
    });
    whiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        $scope.$apply(function() {
            initStateEvent.colaborators.forEach(function (collab) {
                $scope.collaborators.push(new Collaborator(collab.userId, collab.username, collab.joined, true, true));
            });
        });
    });
    whiteboardSocketService.registerForSocketEvent('BoardUserCloseEvent', function(boardUserCloseEvent) {
        $scope.$apply(function() {
            $scope.collaborators.forEach(function(collab) {
                if (collab.id === boardUserCloseEvent.userId) {
                    collab.join = false;
                }
            });
        });
    });
}]);