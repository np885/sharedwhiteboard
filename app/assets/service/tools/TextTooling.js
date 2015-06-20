'use strict';

app.service('TextTooling',['AbstractTooling', 'DrawIdService', 'WhiteboardSocketService', 'Events',
    function (abstractTooling, drawIdService, whiteboardSocketService, Events) {
        function TextTooling() {
            this.mouseMove = function(event){/*Do Nothing*/};
            this.mouseUp = function(event){/*Do Nothing*/};

            this.mouseDown =  function(event){
                if(this.drawing) {
                    //drawing is true if at least one letter was written.
                    var drawFinishedEvent  = new Events.DrawFinishedEvent('TextEvent', drawIdService.getCurrent() - 1);
                    whiteboardSocketService.send(JSON.stringify(drawFinishedEvent));
                    this.drawing = false; //mouseDown = new id = new textelement = no letter written yet = drawing is false.
                }

                this.getCurrentMouse(event);

                var id = drawIdService.getCurrent();
                drawIdService.incrementId();
                var input = document.getElementById('drawText');
                //move input to click position to prevent "jumping" to hidden element on type:
                input.style['margin-left'] = this.currentX + 'px';
                input.style['margin-top'] = this.currentY-24 + 'px';
                //set focus without "jumping" to hidden element on click:
                var x = window.scrollX, y = window.scrollY;
                input.focus();
                window.scrollTo(x, y);

                input.value = '';
                var textEvent = new Events.TextEvent(id, this.currentX, this.currentY, input.value);
                textEvent.cursorPosition = input.selectionStart;
                whiteboardSocketService.sendEventPrivate(textEvent);
                var dirtyHelper = this;
                input.onkeyup = function (event) {
                    dirtyHelper.drawing = true;
                    var textEvent = new Events.TextEvent(id, dirtyHelper.currentX, dirtyHelper.currentY, input.value);
                    textEvent.cursorPosition = input.selectionStart;
                    whiteboardSocketService.sendEventPrivate(textEvent);
                    whiteboardSocketService.send(JSON.stringify(textEvent));
                };

            };
        }
        TextTooling.prototype = abstractTooling;

        return new TextTooling();
    }]
);