'use strict';

app.controller('MainController', ['$scope', 'AuthenticationService', 'ListSocketService', '$location',
    function($scope, AuthenticationService, listSocketService, $location){

        $scope.isLoggedIn = function(){
            //Check if User is logged in
            $scope.currentUser = AuthenticationService.getUser();
            return AuthenticationService.isAuthenticated();
        };

        $scope.isWhiteboardDetail = function(){
            //Check if WhiteboardDetails Page is open
            return $location.url().indexOf('/whiteboard/') > -1;
        };

        $scope.logout = function(){
            listSocketService.closeConnection();
            AuthenticationService.clearCredentials();
        };


    }]);

