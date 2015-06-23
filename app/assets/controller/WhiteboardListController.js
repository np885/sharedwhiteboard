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
                $scope.onlinelist = {};
                data.forEach(function(userElement) {
                    $scope.onlinelist[userElement.id] =
                        (typeof userElement.currentlyJoinedBoardId === 'undefined')
                            ? null
                            : userElement.currentlyJoinedBoardId;
                });
            })
            .error(function (data, status, headers, config) {
                //ToDO: error
            });
    };

    $scope.isOnline = function(userId) {
        return $scope.onlinelist.hasOwnProperty(userId);
    };

    $scope.isJoined = function(userId, whiteboardId) {
        if ($scope.onlinelist.hasOwnProperty(userId)) {
            return $scope.onlinelist[userId] === whiteboardId;
        }
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

    listSocketService.registerForSocketEvent('ListStateChangedEvent', function(event) {
            if (event.structuralChanges) {
                $scope.$apply($scope.loadWhiteboards);
            } else {
                $scope.$apply($scope.refreshOnlineList);
            }
    });
    listSocketService.registerForSocketEvent('BoardUserOnlineEvent', $scope.refreshOnlineList);
    listSocketService.registerForSocketEvent('BoardUserOfflineEvent', $scope.refreshOnlineList);

    listSocketService.openSocketConnection(function() {
        $scope.$apply(function() {
            $scope.onlinelist = [];
            $scope.whiteboards = [];
            $scope.addWhiteboard = function() {
              AuthenticationService.clearCredentials();
            };
        });
        AuthenticationService.doubleLoginDetected();
        AuthenticationService.clearCredentials();

        console.log("double-login currently not allowed!");
    });
    $scope.refreshOnlineList();
    $scope.loadWhiteboards();
}]);