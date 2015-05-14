'use strict';

app.controller('WhiteboardOnlineListController', ['$scope', 'WhiteboardSocketService',
function($scope, whiteboardSocketService){
    function Collaborator(id, name, online) {
        this.id = id;
        this.name = name;
        this.online = (typeof online != 'undefined') ? online : false;
        this.toString = function() {return "{" + this.id + "," + this.name + "}"}
    }

    $scope.collaborators = [];

    whiteboardSocketService.registerForSocketEvent('BoardUserOpenEvent', function(boardUserOpenEvent) {
        $scope.$apply(function() {
            //need scope-apply cause we are out of angular digest cycle, when the server sends events and calls this callback
            $scope.collaborators.push(new Collaborator(boardUserOpenEvent.userId, boardUserOpenEvent.username, true));
        });
    });
    whiteboardSocketService.registerForSocketEvent('InitialBoardStateEvent', function(initStateEvent) {
        $scope.$apply(function() {
            initStateEvent.colaborators.forEach(function (collab) {
                $scope.collaborators.push(new Collaborator(collab.userId, collab.username, true));
            });
        });
    });
    whiteboardSocketService.registerForSocketEvent('BoardUserCloseEvent', function(boardUserCloseEvent) {
        $scope.$apply(function() {
            $scope.collaborators = $scope.collaborators.filter(function (colab) {
                //filter function: return true if element should sty in array
                //  => delete the disco-User:
                return colab.name !== boardUserCloseEvent.username;
            });
        });
    });
}]);