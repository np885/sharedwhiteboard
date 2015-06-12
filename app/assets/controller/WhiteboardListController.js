'use strict';

app.controller('WhiteboardListController', ['$scope', '$modal', 'AuthenticationService', '$http', 'WhiteboardSocketService', 'ListSocketService',
function($scope, $modal, AuthenticationService, $http, WhiteboardSocketService, listSocketService){
    function Whiteboard(id, name, owner, collaborators, socket){
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.collaborators = collaborators;
        this.socket = socket;
    }

    $scope.currentUser = AuthenticationService.getUser();

    $scope.whiteboards = [];

    $scope.prepareOpening = function(whiteboard){
        WhiteboardSocketService.setWhiteboard(whiteboard);
    };

    $scope.loadWhiteboards = function(){
        $http.get('/whiteboards')
            .success(function(data, status, headers, config) {
                $scope.whiteboards = [];
                $scope.transform(data);
            })
            .error(function (data, status, headers, config) {
                //ToDO: error
            });
    };

    $scope.transform = function(data){
        for(var i = 0; i < data.boards.length; i++){
            var collaborators = [];
            var whiteboard = data.boards[i];
            var mappedWhiteboard = new Whiteboard(whiteboard.id, whiteboard.name, whiteboard.owner.description.username, whiteboard.collaborators, whiteboard.socket.href);
            $scope.whiteboards.push(mappedWhiteboard);
        }
    };

    $scope.addWhiteboard = function(){
        var modalInstance = $modal.open({
            templateUrl: 'assets/view/whiteboardadd.html',
            controller: 'WhiteboardAddController',
            resolve: {}
        });

        modalInstance.result.then(function () {
            $scope.loadWhiteboards();
        }, function () {
            //Dissmiss do nothing
        });
    };

    $scope.loadWhiteboards();
    listSocketService.openSocketConnection();
}]);