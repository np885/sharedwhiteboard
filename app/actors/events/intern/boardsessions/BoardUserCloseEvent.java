package actors.events.intern.boardsessions;

import actors.WebSocketConnection;
import model.user.entities.User;

public class BoardUserCloseEvent extends BoardSessionEvent {

    public BoardUserCloseEvent(WebSocketConnection connection) {
        super(connection);
    }
}
