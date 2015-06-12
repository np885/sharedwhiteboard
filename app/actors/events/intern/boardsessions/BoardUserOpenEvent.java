package actors.events.intern.boardsessions;

import actors.BoardSocketConnection;

public class BoardUserOpenEvent extends BoardSessionEvent {

    public BoardUserOpenEvent(BoardSocketConnection connection) {
        super(connection);
    }
}
