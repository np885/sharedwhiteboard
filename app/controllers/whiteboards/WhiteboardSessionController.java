package controllers.whiteboards;

import actors.WebSocketInActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.common.security.AuthRequired;
import play.libs.F;
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
    public static WebSocket<String> connectToWhiteboard(final long boardId) {
        //TODO test

        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            @Override
            public Props apply(ActorRef outActor) throws Throwable {
                return Props.create(WebSocketInActor.class, outActor, boardId);
            }
        });
    }
}
