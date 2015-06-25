package actors.events.intern.boardsessions;

import actors.board.BoardSocketConnection;

public class BoardUserCloseEvent extends AbstractBoardSessionEvent {

    public BoardUserCloseEvent(BoardSocketConnection connection) {
        super(connection);
    }
}
