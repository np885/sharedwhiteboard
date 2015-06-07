'use strict';

app.controller('WhiteboardSidebarController', ['$scope', 'WhiteboardSocketService', 'constant',
function($scope, whiteboardSocketService, constant){
    function Collaborator(id, name, join, online) {
        this.id = id;
        this.name = name;
        //TODO: online != join. online -> logged-in in App. join -> logged-in Whiteboard.
        this.online = (typeof online != 'undefined') ? online : false;
        this.join = (typeof join != 'undefined') ? join : false;
        this.owner = whiteboardSocketService.checkOwner(id);
        this.toString = function() {return "{" + this.id + "," + this.name + "}";}
    }
    function WhiteboardLog(id, name, typ, logDate){
        this.id = id;
        this.name = name;
        this.typ = typ;
        this.logDate = new Date(logDate);
        this.dateForHtml = this.logDate.getHours() + ":" + this.logDate.getMinutes() + ":" + this.logDate.getSeconds();
        this.typForHtml = "";
        switch (this.typ){
            case "FreeHandEvent":
                this.typForHtml = "zeichnete freihand";
                break;
            case "LineEvent":
                this.typForHtml = "zeichnete eine Linie";
                break;
            case "MoveEvent":
                this.typForHtml = "verschob ein Objekt";
        }
        this.forHTML = "[" + this.dateForHtml + "] " + this.name + " " + this.typForHtml;
    }

    $scope.collaborators = [];
    $scope.whiteboardlog = [];

    whiteboardSocketService.registerForSocketEvent('DrawFinishEvent', function(drawEvent){
        $scope.$apply(function(){
            $scope.whiteboardlog.unshift(new WhiteboardLog(drawEvent.boardElementId, drawEvent.user.username, drawEvent.drawType, drawEvent.logDate));
        });
    });

    whiteboardSocketService.registerForSocketEvent('BoardUserOfflineEvent', function(userOfflineEvent){
        $scope.$apply(function(){
            $scope.collaborators.forEach(function(collab) {
                if(collab.id === userOfflineEvent.user.userId){
                    collab.online = false;
                    collab.join = false;
                }
            });
        });
    });

    whiteboardSocketService.registerForSocketEvent('BoardUserOnlineEvent', function(userOnlineEvent){
        $scope.$apply(function(){
            $scope.collaborators.forEach(function(collab) {
                if(collab.id === userOnlineEvent.user.userId){
                    collab.online = true;
                }
            });
        });
    });

    whiteboardSocketService.registerForSocketEvent('BoardUserOpenEvent', function(boardUserOpenEvent) {
        //-> user joining...
        $scope.$apply(function() {
            //need scope-apply cause we are out of angular digest cycle, when the server sends events and calls this callback
            var alreadyMember = false;
            $scope.collaborators.forEach(function(collab) {
                if (collab.id === boardUserOpenEvent.user.userId) {
                    collab.join = true;
                    alreadyMember = true;
                }
            });
            if (!alreadyMember) {
                $scope.collaborators.push(new Collaborator(boardUserOpenEvent.user.userId, boardUserOpenEvent.user.username, true, true));
            }
        });
    });
    whiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        $scope.$apply(function() {
            initStateEvent.colaborators.forEach(function (collab) {
                $scope.collaborators.push(new Collaborator(collab.user.userId, collab.user.username, collab.joined, collab.online));
            });
            initStateEvent.activityLog.forEach(function(drawEvent){
                $scope.whiteboardlog.push(new WhiteboardLog(drawEvent.boardElementId, drawEvent.user.username, drawEvent.drawType, drawEvent.logDate));
            });
        });
    });
    whiteboardSocketService.registerForSocketEvent('BoardUserCloseEvent', function(boardUserCloseEvent) {
        $scope.$apply(function() {
            $scope.collaborators.forEach(function(collab) {
                if (collab.id === boardUserCloseEvent.user.userId) {
                    collab.join = false;
                }
            });
        });
    });
}]);