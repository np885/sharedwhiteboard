package actors.events.socket.boardsessions;

import actors.events.socket.boardstate.SimpleUser;
import actors.events.SocketEvent;

/**
 */
public abstract class AbstractBoardUserSocketEvent extends SocketEvent {
    private SimpleUser user;

    public SimpleUser getUser() {
        return user;
    }

    public void setUser(SimpleUser user) {
        this.user = user;
    }
}
