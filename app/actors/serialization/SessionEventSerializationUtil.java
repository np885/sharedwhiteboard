package actors.serialization;

import actors.events.sockets.BoardUserOpenEvent;
import com.fasterxml.jackson.annotation.JsonValue;
import play.libs.Json;

public class SessionEventSerializationUtil {
    //TODO doc
    public static String serialize(BoardUserOpenEvent event) {
        //TODO test
        BoardUserOpenEventDTO dto = new BoardUserOpenEventDTO();
        dto.setUserId(event.getUser().getId());
        dto.setUsername(event.getUser().getUsername());

        return Json.stringify(Json.toJson(dto));
    }

}
class BoardUserOpenEventDTO extends AbstractSocketOutDTO{

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
