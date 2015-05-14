package actors.events.socket.boardsessions;

import actors.events.SocketEvent;

public class BoardUserOpenSocketEvent extends AbstractBoardUserSocketEvent {

    @Override
    public String getEventType() {
        return "BoardUserOpenEvent";
    }


}
