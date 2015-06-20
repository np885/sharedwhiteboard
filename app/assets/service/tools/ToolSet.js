'use strict';

app.service('ToolSet',['RectangleTooling',
    function (rectangleTooling) {
        var service = {};

        service.rectangleTooling = rectangleTooling;

        return service;
    }
]);