package actors.events.socket.boardstate;

import actors.events.SimpleUser;

public class Collab {
    private SimpleUser user;
    private boolean joined = false;

    public Collab(long userId, String username) {
        user = new SimpleUser(userId, username);
    }

    public SimpleUser getUser() {
        return user;
    }

    public void setUser(SimpleUser user) {
        this.user = user;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

}
