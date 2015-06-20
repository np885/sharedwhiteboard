'use strict';

app.service('ToolSet',['RectangleTooling', 'CircleTooling', 'LineTooling', 'FreehandTooling', 'MovementTooling',
    function (rectangleTooling, circleTooling, lineTooling, freehandTooling, movementTooling) {
        var service = {};

        service.rectangleTooling = rectangleTooling;
        service.circleTooling = circleTooling;
        service.lineTooling = lineTooling;
        service.freehandTooling = freehandTooling;
        service.movementTooling = movementTooling;

        return service;
    }
]);