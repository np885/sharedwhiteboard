package actors.events.socket.boardstate;

import actors.events.SocketEvent;
import actors.events.socket.boardstate.drawings.DrawingDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class InitialBoardStateEvent extends SocketEvent {
    private List<Collab> colaborators = new ArrayList<>();

    private List<DrawingDTO> drawings = new ArrayList<>();

    @Override
    public String getEventType() {
        return "InitialBoardStateEvent";
    }

    public List<Collab> getColaborators() {
        return colaborators;
    }

    public List<DrawingDTO> getDrawings() {
        return drawings;
    }
}


