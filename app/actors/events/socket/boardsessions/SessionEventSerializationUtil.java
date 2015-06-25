package actors.events.socket.boardsessions;

import actors.events.intern.app.AbstractAppUserEvent;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.socket.boardstate.SimpleUser;
import actors.events.intern.boardsessions.AbstractBoardSessionEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import play.libs.Json;

public class SessionEventSerializationUtil {
    //TODO doc
    public static String serialize(AbstractBoardSessionEvent event) {
        //TODO test
        AbstractBoardUserSocketEvent dto = (event instanceof BoardUserOpenEvent) ? new BoardUserOpenSocketEvent() : new BoardUserCloseSocketEvent();
        dto.setUser(new SimpleUser(event.getConnection().getUser().getId(), event.getConnection().getUser().getUsername()));
        return Json.stringify(Json.toJson(dto));
    }

    public static String serializeUserAppEvent(AbstractAppUserEvent event){
        AbstractBoardUserSocketEvent dto = (event instanceof AppUserLoginEvent) ? new BoardUserOnlineSocketEvent() : new BoardUserOfflineSocketEvent();
        dto.setUser(new SimpleUser(event.getUser().getId(), event.getUser().getUsername()));
        return Json.stringify(Json.toJson(dto));
    }
}
