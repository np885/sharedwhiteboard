package controllers.whiteboards;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.common.security.AuthRequired;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

/**
 * Created by Flo on 06.05.2015.
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
