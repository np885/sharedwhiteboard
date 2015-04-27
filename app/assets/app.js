'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('sharedwhiteboard', ['ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.
        when('/login', {
            templateUrl: 'assets/view/login.html',
            controller: 'LoginController'
        }).
        when('/whiteboard/:boardId', {
            templateUrl: 'assets/view/whiteboard.html',
            controller: 'WhiteboardController'
        }).
        when('/whiteboardlist', {
            templateUrl: 'assets/view/whiteboardlist.html',
            controller: 'WhiteboardListController'
        }).
        otherwise({
            redirectTo: '/login'
        });
}]);

