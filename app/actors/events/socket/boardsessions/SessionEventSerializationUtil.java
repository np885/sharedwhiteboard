package actors.events.socket.boardsessions;

import actors.events.SimpleUser;
import actors.events.intern.boardsessions.BoardSessionEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import play.libs.Json;

public class SessionEventSerializationUtil {
    //TODO doc
    public static String serialize(BoardSessionEvent event) {
        //TODO test
        AbstractBoardUserSocketEvent dto = (event instanceof BoardUserOpenEvent) ? new BoardUserOpenSocketEvent() : new BoardUserCloseSocketEvent();
        dto.setUser(new SimpleUser(event.getConnection().getUser().getId(), event.getConnection().getUser().getUsername()));
        return Json.stringify(Json.toJson(dto));
    }
}
