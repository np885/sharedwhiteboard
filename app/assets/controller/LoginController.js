'use strict';

app.controller('LoginController', ['$scope', 'AuthenticationService', '$location', '$modal',
    function($scope, AuthenticationService, $location, $modal){

    $scope.user = {};
    $scope.user.username = '';
    $scope.user.password = '';

    $scope.login = function () {
        AuthenticationService.login($scope.user)
            .success(function(data, status, headers, config) {
                AuthenticationService.setCredentials($scope.user);
                $location.path('/whiteboardlist');
            })
            .error(function (data, status, headers, config) {
                AuthenticationService.clearCredentials();
                $scope.error = 'Die Anmeldung ist fehlgeschlagen';
            });
    };

    $scope.register = function () {

        var modalInstance = $modal.open({
            templateUrl: 'assets/view/registermodal.html',
            controller: 'RegisterController',
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