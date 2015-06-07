package actors.events.socket.boardsessions;

/**
 * Created by niclas on 07.06.15.
 */
public class BoardUserOfflineSocketEvent extends AbstractBoardUserSocketEvent{
    @Override
    public String getEventType() {
        return "BoardUserOfflineEvent";
    }
}
