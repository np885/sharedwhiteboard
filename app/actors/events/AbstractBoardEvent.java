package actors.events;

public abstract class AbstractBoardEvent {
    private String boardName;

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}
