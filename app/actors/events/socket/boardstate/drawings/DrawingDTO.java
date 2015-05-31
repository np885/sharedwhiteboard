package actors.events.socket.boardstate.drawings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 */
public class DrawingDTO {
    @JsonProperty
    private final String type;

    private int boardElementId;

    public DrawingDTO(String type) {
        this.type = type;
    }

    public int getBoardElementId() {
        return boardElementId;
    }

    public void setBoardElementId(int boardElementId) {
        this.boardElementId = boardElementId;
    }
}
