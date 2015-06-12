'use strict';

app.controller('WhiteboardSidebarController', ['$scope', 'WhiteboardSocketService', 'constant',
function($scope, whiteboardSocketService, constant){
    function Collaborator(id, name, join, online) {
        this.id = id;
        this.name = name;
        this.online = (typeof online != 'undefined') ? online : false;
        this.join = (typeof join != 'undefined') ? join : false;
        this.owner = whiteboardSocketService.checkOwner(name);
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
                break;
            case "RectangleEvent":
                this.typForHtml = "zeichnete ein Rechteck";
                break;
            case "CircleEvent":
                this.typForHtml = "zeichnete einen Kreis";
                break;
            case "TextEvent":
                this.typForHtml = "schrieb ein Text";
                break;
            case "BoardUserOnlineEvent":
                this.typForHtml = "ist online!";
                break;
            case "BoardUserOfflineEvent":
                this.typForHtml = "ist offline!";
                break;
            case "BoardUserOpenEvent":
                this.typForHtml = "ist der Session beigetreten!";
                break;
            case "BoardUserCloseEvent":
                this.typForHtml = "ist aus der Session ausgetreten!";
                break;
        }
        this.forHTML = "[" + this.dateForHtml + "] " + this.name + " " + this.typForHtml;
    }


    //logs everything, that is not a draw event.
    function genericLogFunction(event) {
        $scope.$apply(function(){
            $scope.whiteboardlog.unshift(new WhiteboardLog(
                null,
                event.user.username,
                event.eventType,
                new Date()
            ));
        });
    }
    whiteboardSocketService.registerForSocketEvent('BoardUserOnlineEvent', genericLogFunction);
    whiteboardSocketService.registerForSocketEvent('BoardUserOfflineEvent', genericLogFunction);
    whiteboardSocketService.registerForSocketEvent('BoardUserOpenEvent', genericLogFunction);
    whiteboardSocketService.registerForSocketEvent('BoardUserCloseEvent', genericLogFunction);



    $scope.collaborators = [];
    $scope.whiteboardlog = [];

    whiteboardSocketService.registerForSocketEvent('DrawFinishEvent', function(drawEvent){
        $scope.$apply(function(){
            $scope.whiteboardlog.unshift(new WhiteboardLog(
                drawEvent.boardElementId,
                drawEvent.user.username,
                drawEvent.drawType,
                drawEvent.logDate
            ));
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