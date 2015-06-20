'use strict';

app.service('ToolSet',['RectangleTooling', 'CircleTooling', 'LineTooling',
    function (rectangleTooling, circleTooling, lineTooling) {
        var service = {};

        service.rectangleTooling = rectangleTooling;
        service.circleTooling = circleTooling;
        service.lineTooling = lineTooling;

        return service;
    }
]);