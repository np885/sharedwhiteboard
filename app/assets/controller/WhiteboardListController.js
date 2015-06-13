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
    $scope.onlinelist = [];

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

    $scope.refreshOnlineList = function() {
        $http.get('/users/online')
            .success(function(data, status, headers, config) {
                $scope.onlinelist = data.map(function(u) {return u.id});
                console.log($scope.onlinelist);
            })
            .error(function (data, status, headers, config) {
                //ToDO: error
            });
    };

    $scope.isOnline = function(userId) {
        return $scope.onlinelist.indexOf(userId) >= 0;
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

    listSocketService.registerForSocketEvent('ListStateChangedEvent', $scope.loadWhiteboards);
    listSocketService.registerForSocketEvent('BoardUserOnlineEvent', $scope.refreshOnlineList);
    listSocketService.registerForSocketEvent('BoardUserOfflineEvent', $scope.refreshOnlineList);

    listSocketService.openSocketConnection();
    $scope.refreshOnlineList();
    $scope.loadWhiteboards();
}]);