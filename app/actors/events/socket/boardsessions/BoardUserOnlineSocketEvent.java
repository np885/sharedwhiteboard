package actors.events.socket.boardsessions;

/**
 * Created by niclas on 07.06.15.
 */
public class BoardUserOnlineSocketEvent extends AbstractBoardUserSocketEvent{
    @Override
    public String getEventType() {
        return "BoardUserOnlineEvent";
    }
}
