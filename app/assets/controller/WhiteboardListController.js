'use strict';

app.controller('WhiteboardListController', ['$scope', '$modal', 'AuthenticationService', '$http', function($scope, $modal, AuthenticationService, $http){
    $scope.currentUser = AuthenticationService.getUser();

    $scope.whiteboards = [];
    $scope.whitboardWithMeta = {};

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
            $scope.whiteboards.push({name: whiteboard.name, id: whiteboard.id, owner: whiteboard.owner.description.username, collaborators: collaborators});
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