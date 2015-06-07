package actors.events.socket.boardstate;

public class CollabState {
    private SimpleUser user;
    private boolean joined = false;
    private boolean online = false;

    public CollabState(long userId, String username) {
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
