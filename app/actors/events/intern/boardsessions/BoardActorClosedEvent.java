package actors.events.intern.boardsessions;

import actors.WebSocketConnection;

/**
 */
public class BoardActorClosedEvent {

    private long boardId;

    public BoardActorClosedEvent(long boardId) {
        this.boardId = boardId;
    }

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
