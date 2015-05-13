package actors.events.socket.boardsessions;

import play.libs.Json;

public class SessionEventSerializationUtil {
    //TODO doc
    public static String serialize(actors.events.intern.boardsessions.BoardUserOpenEvent event) {
        //TODO test
        BoardUserOpenEvent dto = new BoardUserOpenEvent();
        dto.setUserId(event.getConnection().getUser().getId());
        dto.setUsername(event.getConnection().getUser().getUsername());

        return Json.stringify(Json.toJson(dto));
    }

}
