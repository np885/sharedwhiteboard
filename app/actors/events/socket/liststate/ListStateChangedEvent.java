package actors.events.socket.liststate;

import actors.events.socket.boardsessions.AbstractBoardUserSocketEvent;

/**
 * Created by Flo on 13.06.2015.
 */
public class ListStateChangedEvent extends AbstractBoardUserSocketEvent {

    /**
     * true if whole board list have to be reloaded. If only online or joined status changed, it can be set to false,
     * to tell the client that a 'small refresh' is ok.
     */
    private boolean structuralChanges = true;

    @Override
    public String getEventType() {
        return "ListStateChangedEvent";
    }

    public boolean isStructuralChanges() {
        return structuralChanges;
    }

    public void setStructuralChanges(boolean structuralChanges) {
        this.structuralChanges = structuralChanges;
    }
}
