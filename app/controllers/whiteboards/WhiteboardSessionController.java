package controllers.whiteboards;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.common.security.AuthRequired;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

/**
  * Handles API Requests for Socket-Connections to Whiteboards.
  *
  */
public class WhiteboardSessionController extends Controller {

    //TODO doc
    @AuthRequired
    public static WebSocket<JsonNode> connectToWhiteboard(long id) {
        //TODO impl
        //TODO test
        return null;
    }
}
