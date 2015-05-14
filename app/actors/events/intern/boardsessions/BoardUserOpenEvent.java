package actors.events.intern.boardsessions;

import actors.WebSocketConnection;

public class BoardUserOpenEvent extends BoardSessionEvent {

    public BoardUserOpenEvent(WebSocketConnection connection) {
        super(connection);
    }
}
