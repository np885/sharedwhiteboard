'use strict';

app.controller('WhiteboardAddController', ['$scope', '$modalInstance',
    function($scope, $modalInstance) {

    $scope.whiteboardname = '';

    $scope.addWhiteboard = function () {

    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}]);