'use strict';

app.controller('RegisterController', ['$scope', '$modalInstance', 'AuthenticationService',
    function($scope, $modalInstance, AuthenticationService) {

    $scope.user = {};
    $scope.user.username = '';
    $scope.user.password = '';
    $scope.pwveryfication = '';

    $scope.register = function () {
        AuthenticationService.registerUser($scope.user)
            .success(function (data, status, headers, config) {
                //Successfull Registration
                $modalInstance.close($scope.user.username);
            })
            .error(function (data, status, headers, config) {
                //TODO: Check Status for Username Alreadey exists
                $scope.error = 'Die Registrierung ist fehlgeschlagen';
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.checkVerification = function(){
      return $scope.user.password === $scope.pwveryfication;
    };
}]);