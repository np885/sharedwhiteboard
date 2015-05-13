package actors.events.intern.boardsessions;

import actors.WebSocketConnection;

public class BoardUserOpenEvent extends BoardSessionEvent {
    private WebSocketConnection connection;

    public BoardUserOpenEvent(WebSocketConnection connection) {
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
