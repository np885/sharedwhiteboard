package actors.events.intern.boardsessions;

import actors.board.BoardSocketConnection;
import actors.events.ServerInternEvent;

public abstract class AbstractBoardSessionEvent implements ServerInternEvent {
    private BoardSocketConnection connection;

    public AbstractBoardSessionEvent(BoardSocketConnection connection) {
        this.connection = connection;
    }

    public BoardSocketConnection getConnection() {
        return connection;
    }

    public void setConnection(BoardSocketConnection connection) {
        this.connection = connection;
    }

    public long getBoardId(){
        return connection.getBoardId();
    }
}
