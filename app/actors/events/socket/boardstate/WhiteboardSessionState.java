package actors.events.socket.boardstate;

import actors.events.socket.draw.DrawFinishedEvent;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;

import java.util.*;

public class WhiteboardSessionState {

    //This represents the activityLog of the Whiteboard, should not be persist
    private LinkedList<DrawFinishedEvent> activityLog = new LinkedList<>();

    //This is special collaboratorState which is only available while Whitboardactor lives
    private List<CollabState> collabs = new ArrayList<>();

    public List<CollabState> getCollabs() {
        return collabs;
    }

    public void setCollabs(List<CollabState> collabs) {
        this.collabs = collabs;
    }

    public LinkedList<DrawFinishedEvent> getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(LinkedList<DrawFinishedEvent> activityLog) {
        this.activityLog = activityLog;
    }

    public void changeCollabStateOnline(long userId, Boolean online) {
        changeCollabState(userId, online, null);
    }

    public void changeCollabStateJoin(long userId, Boolean join) {
        changeCollabState(userId, null, join);
    }

    public void changeCollabState(long userId, Boolean online, Boolean join){
        for(CollabState collab : collabs){
            if(collab.getUser().getUserId() == userId){
                if(join != null) {
                    collab.setJoined(join);
                }
                if(online != null) {
                    collab.setOnline(online);
                }
            }
        }
    }

    public void initCollabStates(Whiteboard currentState, List<User> onlineUsers) {
        //Add all collaborators from Whiteboard
        collabs.addAll(BoardStateSerializationUtil.mapToCollabState(currentState));

        //Change onlineState of onlineUsers of this Board
        Set<Long> onlineIds = new HashSet<>();
        for (User user : onlineUsers) {
            onlineIds.add(user.getId());
        }
        for (User user : currentState.getCollaborators()) {
            if (onlineIds.contains(user.getId())) {
                changeCollabStateOnline(user.getId(), true);
            }
        }
    }
}
