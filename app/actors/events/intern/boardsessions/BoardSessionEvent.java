package actors.events.intern.boardsessions;

import actors.WebSocketConnection;
import actors.events.ServerInternEvent;

public abstract class BoardSessionEvent implements ServerInternEvent {
    private WebSocketConnection connection;

    public BoardSessionEvent(WebSocketConnection connection) {
        this.connection = connection;
    }

    public WebSocketConnection getConnection() {
        return connection;
    }

    public void setConnection(WebSocketConnection connection) {
        this.connection = connection;
    }

    public long getBoardId(){
        return connection.getBoardId();
    }
}
