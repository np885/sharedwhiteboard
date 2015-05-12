package actors.events;

public abstract class AbstractBoardEvent {

    private long boardId;

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
