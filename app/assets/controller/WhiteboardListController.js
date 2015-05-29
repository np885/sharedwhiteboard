'use strict';

app.controller('WhiteboardListController', ['$scope', '$modal', 'AuthenticationService', '$http', 'WhiteboardSocketService',
function($scope, $modal, AuthenticationService, $http, WhiteboardSocketService){
    function Whiteboard(id, name, owner, collaborators, socket){
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.collaborators = collaborators;
        this.socket = socket;
    }

    $scope.currentUser = AuthenticationService.getUser();

    $scope.whiteboards = [];
    $scope.whitboardWithMeta = {};

    //TODO: We have to rework something here. At the end this should also be reactive with Socket connection.
    $scope.dummyCollabs = [
        {name: 'niclas', online: true, join: true},
        {name: 'peter', online: false, join: true},
        {name: 'hans', online: false, join: false}];

    $scope.openWhiteboardSocket = function(whiteboard){
        WhiteboardSocketService.setWebsocketPath(whiteboard.socket);
    };

    $scope.loadWhiteboard = function(){
        $http.get('/whiteboards')
            .success(function(data, status, headers, config) {
                $scope.whiteboards = [];
                $scope.transform(data);
                $scope.whitboardWithMeta = data;
            })
            .error(function (data, status, headers, config) {
                //ToDO: error
            });
    };

    $scope.transform = function(data){
        for(var i = 0; i < data.boards.length; i++){
            var collaborators = [];
            var whiteboard = data.boards[i];
            for(var j = 0; j < whiteboard.collaborators.length; j++){
                var user = whiteboard.collaborators[j];
                collaborators.push({name: user.description.username});
            }
            $scope.whiteboards.push(new Whiteboard(whiteboard.id, whiteboard.name, whiteboard.owner.description.username, collaborators, whiteboard.socket.href));
        }
    };

    $scope.addWhiteboard = function(){
        var modalInstance = $modal.open({
            templateUrl: 'assets/view/whiteboardadd.html',
            controller: 'WhiteboardAddController',
            resolve: {}
        });

        modalInstance.result.then(function () {
            $scope.loadWhiteboard();
        }, function () {
            //Dissmiss do nothing
        });
    };
    $scope.loadWhiteboard();
}]);