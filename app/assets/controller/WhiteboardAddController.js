'use strict';

app.controller('WhiteboardAddController', ['$scope', '$modalInstance', '$http',
    function($scope, $modalInstance, $http) {

    $scope.whiteboardname = {};
    $scope.whiteboardname.name = '';

    $scope.addWhiteboard = function () {
    $http.post('/whiteboards', $scope.whiteboardname)
            .success(function(data, status, headers, config) {
                $modalInstance.close();
            })
            .error(function (data, status, headers, config) {
                if (status === 422) {
                    //semantic error:
                    $scope.error = data;
                } else {
                    $scope.error = 'Die Registrierung ist fehlgeschlagen';
                }
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}]);