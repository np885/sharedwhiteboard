package actors.serialization.boardsessions;

import actors.events.sockets.BoardUserOpenEvent;
import play.libs.Json;

public class SessionEventSerializationUtil {
    //TODO doc
    public static String serialize(BoardUserOpenEvent event) {
        //TODO test
        BoardUserOpenEventDTO dto = new BoardUserOpenEventDTO();
        dto.setUserId(event.getConnection().getUser().getId());
        dto.setUsername(event.getConnection().getUser().getUsername());

        return Json.stringify(Json.toJson(dto));
    }

}
