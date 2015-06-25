package actors.events.intern.boardsessions;

import actors.events.ServerInternEvent;

/**
 */
public class BoardActorClosedEvent implements ServerInternEvent{

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
