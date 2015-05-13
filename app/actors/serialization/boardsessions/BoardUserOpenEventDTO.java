package actors.serialization.boardsessions;

import actors.serialization.AbstractSocketOutDTO;

public class BoardUserOpenEventDTO extends AbstractSocketOutDTO {

    @Override
    public String getEventType() {
        return "BoardUserOpenEvent";
    }
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
