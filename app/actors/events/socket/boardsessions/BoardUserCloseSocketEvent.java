package actors.events.socket.boardsessions;

import actors.events.SocketEvent;

/**
 */
public class BoardUserCloseSocketEvent extends AbstractBoardUserSocketEvent {
    @Override
    public String getEventType() {
        return "BoardUserCloseEvent";
    }
}
