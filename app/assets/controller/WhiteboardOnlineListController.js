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
        console.log(boardUserOpenEvent);
        $scope.$apply(function() {
            //need scope-apply cause we are out of angular digest cycle, when the server sends events and calls this callback
            $scope.collaborators.push(new Collaborator(boardUserOpenEvent.userId, boardUserOpenEvent.username, true));
        });
    });
}]);