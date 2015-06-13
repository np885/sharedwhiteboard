package actors.events.socket.liststate;

import actors.events.SocketEvent;
import actors.events.socket.boardsessions.AbstractBoardUserSocketEvent;

/**
 * Created by Flo on 13.06.2015.
 */
public class ListStateChangedEvent extends AbstractBoardUserSocketEvent {

    @Override
    public String getEventType() {
        return "ListStateChangedEvent";
    }
}
