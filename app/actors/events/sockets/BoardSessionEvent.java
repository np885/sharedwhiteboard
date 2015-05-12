package actors.events.sockets;

import actors.events.AbstractBoardEvent;
import model.user.entities.User;

public abstract class BoardSessionEvent extends AbstractBoardEvent {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
