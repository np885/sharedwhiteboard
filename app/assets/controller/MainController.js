app.controller('MainController', ['$scope', function($scope){

    $scope.isLoggedIn = function(){
        //Check if User is logged in
        return true;
    };

    $scope.isWhiteboardDetail = function(){
        //Check if WhiteboardDetails Page is open
        return true;
    };
}]);