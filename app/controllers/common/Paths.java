package controllers.common;

import model.whiteboards.entities.Whiteboard;
import play.Play;

/**
 * Behilfsloesung solange die Play-generierten "routes" Klassen rumspinnen.
 */
public class Paths {
    public static final String ROOT = ""; //relative URLs

    public static final String WHITEBOARDS_RELATIVE = "/whiteboards";
    public static final String WHITEBOARDS_FULL = ROOT + WHITEBOARDS_RELATIVE;
    public static final String USERS_FULL = ROOT + "/users";
    public static final String APPLICATION_SOCKET_TICKET = "/login/session/ticket";

    public static String forWhiteboard(Whiteboard wb) {
        return WHITEBOARDS_FULL + "/" + wb.getId();
    }
    public static String forWhiteboardId(long id) {
        return WHITEBOARDS_FULL + "/" + id;
    }


    public static String TicketPathForSockets(Whiteboard whiteboard) {
        return WHITEBOARDS_FULL + "/" + whiteboard.getId() + "/session/ticket";
    }
    public static String SocketPathForWhiteboardId(long boardId) {
        return WHITEBOARDS_FULL.replace("http", "ws") + "/" + boardId + "/session";
    }
}
