package controllers.whiteboards;

import actors.board.BoardSocketInActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.common.Paths;
import controllers.common.security.AuthRequired;
import model.user.entities.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;

import java.util.HashMap;
import java.util.Map;

/**
  * Handles API Requests for Socket-Connections to Whiteboards.
  *
  */
public class WhiteboardSessionController extends Controller {

    private static SocketTicketSystem ticketSystem = new SocketTicketSystem();

    @AuthRequired
    public static Result createTicket(long boardId) {
        //TODO doc
        //TODO test
        //Authenticated User can create ticket for websocket connection.

        HashMap<String, String> properties = new HashMap<>();
        properties.put("boardId", boardId + "");
        String ticketNumber = ticketSystem.createTicket((User) ctx().args.get("currentuser"), properties);

        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.SocketPathForWhiteboardId(boardId) + "/" + ticketNumber);

        return created();
    }

    //TODO doc
    public static WebSocket<String> connectToWhiteboard(final long boardId, final String ticketNumber) {
        //TODO test

         //tickets.get(ticketNumber);
        Map<String, String> desiredProperties = new HashMap<>();
        desiredProperties.put("boardId", boardId + "");
        if (! ticketSystem.validate(ticketNumber, desiredProperties)){
            //TODO timestamp expiration would be necessary for real system.
            return WebSocket.reject(forbidden());
        }

        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            @Override
            public Props apply(ActorRef outActor) throws Throwable {
                User userForValidTicket = ticketSystem.invalidate(ticketNumber);
                return Props.create(BoardSocketInActor.class, outActor, boardId, userForValidTicket);
            }
        });
    }
}
