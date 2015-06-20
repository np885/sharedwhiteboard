'use strict';

app.service('ToolSet',['RectangleTooling', 'CircleTooling', 'LineTooling', 'FreehandTooling',
    function (rectangleTooling, circleTooling, lineTooling, freehandTooling) {
        var service = {};

        service.rectangleTooling = rectangleTooling;
        service.circleTooling = circleTooling;
        service.lineTooling = lineTooling;
        service.freehandTooling = freehandTooling;

        return service;
    }
]);