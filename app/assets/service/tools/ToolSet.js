'use strict';

app.service('ToolSet',['RectangleTooling', 'CircleTooling',
    function (rectangleTooling, circleTooling) {
        var service = {};

        service.rectangleTooling = rectangleTooling;
        service.circleTooling = circleTooling;

        return service;
    }
]);