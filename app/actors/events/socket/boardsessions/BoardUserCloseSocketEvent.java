package actors.events.socket.boardsessions;

/**
 */
public class BoardUserCloseSocketEvent extends AbstractBoardUserSocketEvent {
    @Override
    public String getEventType() {
        return "BoardUserCloseEvent";
    }
}
