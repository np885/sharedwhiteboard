'use strict';

app.service('ToolSet',['RectangleTooling', 'CircleTooling', 'LineTooling', 'FreehandTooling', 'MovementTooling',
    'TextTooling',
    function (rectangleTooling, circleTooling, lineTooling, freehandTooling, movementTooling, textTooling) {
        var service = {};

        service.rectangleTooling = rectangleTooling;
        service.circleTooling = circleTooling;
        service.lineTooling = lineTooling;
        service.freehandTooling = freehandTooling;
        service.movementTooling = movementTooling;
        service.textTooling = textTooling;

        return service;
    }
]);