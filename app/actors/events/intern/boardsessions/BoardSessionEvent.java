package actors.events.intern.boardsessions;

import actors.BoardSocketConnection;
import actors.events.ServerInternEvent;

public abstract class BoardSessionEvent implements ServerInternEvent {
    private BoardSocketConnection connection;

    public BoardSessionEvent(BoardSocketConnection connection) {
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
