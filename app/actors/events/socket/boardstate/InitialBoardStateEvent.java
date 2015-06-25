package actors.events.socket.boardstate;

import actors.events.AbstractSocketEvent;
import actors.events.socket.boardstate.drawings.DrawingDTO;
import actors.events.socket.draw.DrawFinishedEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class InitialBoardStateEvent extends AbstractSocketEvent {
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


