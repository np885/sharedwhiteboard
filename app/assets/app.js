'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('sharedwhiteboard', ['ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider', '$httpProvider',
function($routeProvider, $httpProvider) {
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
    $httpProvider.interceptors.push(function() {
        return {
            'request': function (config) {
                if (sessionStorage.authData != null) {
                    config.headers['Authorization'] = 'Basic ' + sessionStorage.authData.replace('"', '');
                }
                return config;
            },

            'response': function (response) {
                return response;
            }
        };
    });
}]);

app.run(['$rootScope', '$location', 'AuthenticationService', 'WhiteboardSocketService',
    function ($rootScope, $location, AuthenticationService, WhiteboardSocketService) {

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
            if(next.$$route && next.$$route.originalPath === "/login" && AuthenticationService.isAuthenticated()){
                event.preventDefault();
                $rootScope.$evalAsync(function() {
                    $location.path('/whiteboardlist');
                });
            }
            if(current && current.$$route && current.$$route.originalPath.indexOf('/whiteboard/') > -1 && current !== next){
                WhiteboardSocketService.closeConnection();
            }
        });
    }]);