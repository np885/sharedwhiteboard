package actors.events.intern.boardsessions;

import actors.board.BoardSocketConnection;

public class BoardUserOpenEvent extends BoardSessionEvent {

    public BoardUserOpenEvent(BoardSocketConnection connection) {
        super(connection);
    }
}
