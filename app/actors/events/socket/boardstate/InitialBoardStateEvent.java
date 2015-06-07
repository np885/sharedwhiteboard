package actors.events.socket.boardstate;

import actors.events.SocketEvent;
import actors.events.socket.boardstate.drawings.DrawingDTO;
import actors.events.socket.draw.DrawFinishedEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class InitialBoardStateEvent extends SocketEvent {
    private List<CollabState> colaborators = new ArrayList<>();

    private List<DrawingDTO> drawings = new LinkedList<>();

    private List<DrawFinishedEvent> activityLog = new ArrayList<>();

    @Override
    public String getEventType() {
        return "InitialBoardStateEvent";
    }

    public List<CollabState> getColaborators() {
        return colaborators;
    }

    public List<DrawingDTO> getDrawings() {
        return drawings;
    }

    public List<DrawFinishedEvent> getActivityLog() {
        return activityLog;
    }
}


