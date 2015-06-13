package actors.events.socket.boardstate;


import model.user.entities.User;

public class SimpleUser {
    private long userId;
    private String username;

    public SimpleUser() {
    }

    public SimpleUser(long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public SimpleUser(User userEntity) {
        this.userId = userEntity.getId();
        this.username = userEntity.getUsername();
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
