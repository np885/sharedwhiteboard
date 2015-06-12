'use strict';

app.controller('MainController', ['$scope', 'AuthenticationService', '$location',
    function($scope, AuthenticationService, $location){

        $scope.isLoggedIn = function(){
            //Check if User is logged in
            return AuthenticationService.isAuthenticated();
        };

        $scope.isWhiteboardDetail = function(){
            //Check if WhiteboardDetails Page is open
            return $location.url().indexOf('/whiteboard/') > -1;
        };

        $scope.logout = function(){
            AuthenticationService.clearCredentials();
        };


        $scope.currentUser = AuthenticationService.getUser();

    }]);

