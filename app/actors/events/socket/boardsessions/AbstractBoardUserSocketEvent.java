package actors.events.socket.boardsessions;

import actors.events.SocketEvent;

/**
 */
public abstract class AbstractBoardUserSocketEvent extends SocketEvent {
    private long userId;
    private String username;

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
