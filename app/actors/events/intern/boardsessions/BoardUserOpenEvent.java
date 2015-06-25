package actors.events.intern.boardsessions;

import actors.board.BoardSocketConnection;

public class BoardUserOpenEvent extends AbstractBoardSessionEvent {

    public BoardUserOpenEvent(BoardSocketConnection connection) {
        super(connection);
    }
}
