'use strict';

app.controller('WhiteboardListController', ['$scope', '$modal', function($scope, $modal){
    $scope.loggedInUser = {};
    $scope.loggedInUser.id = 1;
    $scope.whiteboards = [
        {name: 'Mein Malboard', id: 1, owner: 1, users:[{id: 1, name: 'Niclas'}, {id: 2, name: 'Peter'}, {id: 3, name: 'Hans'}]},
        {name: 'Brainstorming', id: 2, owner: 2, users:[{id: 2, name: 'Peter'}, {id: 3, name: 'Hans'}]}];

    $scope.addWhiteboard = function(){
        var modalInstance = $modal.open({
            templateUrl: 'assets/view/whiteboardadd.html',
            controller: 'WhiteboardAddController',
            resolve: {}
        });

        modalInstance.result.then(function (username) {
            $scope.user.username = username;
            $scope.user.password = '';
            $scope.error = null;

        }, function () {
            //Dissmiss do nothing
        });
    };
}]);