package actors.events.socket.boardstate;

public class Collab {
    private long userId;
    private String username;
    private boolean online = false;

    public Collab(long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
