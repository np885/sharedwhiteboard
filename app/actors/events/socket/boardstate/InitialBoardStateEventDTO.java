package actors.events.socket.boardstate;

import actors.events.SocketEvent;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class InitialBoardStateEventDTO extends SocketEvent {
    private List<Collab> colaborators = new ArrayList<>();

    @Override
    public String getEventType() {
        return "InitialBoardStateEvent";
    }

    public List<Collab> getColaborators() {
        return colaborators;
    }
}


