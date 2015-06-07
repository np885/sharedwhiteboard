package actors.events.intern.app;


import actors.events.ServerInternEvent;
import model.user.entities.User;

public abstract class AbstractAppUserEvent implements ServerInternEvent{
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
