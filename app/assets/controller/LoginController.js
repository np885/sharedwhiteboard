'use strict';

app.controller('LoginController', ['$scope', 'AuthenticationService', '$location', '$rootScope',
    function($scope, AuthenticationService, $location, $rootScope){

    $scope.user = {};
    $scope.user.username = '';
    $scope.user.password = '';

    $scope.login = function () {
        AuthenticationService.login($scope.user)
            .success(function () {
                AuthenticationService.setCredentials($scope.user);
                $location.path('/whiteboardlist');
            })
            .error(function (data, status, headers, config) {
                AuthenticationService.clearCredentials();
                $scope.error = 'Die Anmeldung ist fehlgeschlagen';
            });
    };

}]);