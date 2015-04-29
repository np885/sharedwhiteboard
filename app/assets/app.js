'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('sharedwhiteboard', ['ngRoute', 'ui.bootstrap', 'ngStorage']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.
        when('/login', {
            templateUrl: 'assets/view/login.html',
            controller: 'LoginController',
            resolve : {
                secure: function(){return false;}
            }
        }).
        when('/whiteboard/:boardId', {
            templateUrl: 'assets/view/whiteboard.html',
            controller: 'WhiteboardController',
            resolve : {
                secure: function(){return true;}
            }
        }).
        when('/whiteboardlist', {
            templateUrl: 'assets/view/whiteboardlist.html',
            controller: 'WhiteboardListController',
            resolve : {
                secure: function(){return true;}
            }
        }).
        otherwise({
            redirectTo: '/login'
        });
}]);

app.run(['$rootScope', '$location', 'AuthenticationService',
    function ($rootScope, $location, AuthenticationService) {

        $rootScope.$on('$routeChangeStart', function (event, next, current) {
            //Check if it's a secure Route
            if(next && next.$$route && next.$$route.resolve.secure()){
                if(!AuthenticationService.isAuthenticated()){
                    event.preventDefault();
                    $rootScope.$evalAsync(function() {
                        $location.path('/login');
                    });
                }
            }
            if(next.$$route.originalPath === "/login" && AuthenticationService.isAuthenticated()){
                event.preventDefault();
                $rootScope.$evalAsync(function() {
                    $location.path('/whiteboardlist');
                });
            }
        });
    }]);