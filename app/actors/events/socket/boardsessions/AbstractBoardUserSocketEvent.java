package actors.events.socket.boardsessions;

import actors.events.socket.boardstate.SimpleUser;
import actors.events.AbstractSocketEvent;

/**
 */
public abstract class AbstractBoardUserSocketEvent extends AbstractSocketEvent {
    private SimpleUser user;

    public SimpleUser getUser() {
        return user;
    }

    public void setUser(SimpleUser user) {
        this.user = user;
    }
}
