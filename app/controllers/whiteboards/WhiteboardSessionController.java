package controllers.whiteboards;

import actors.WebSocketInActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.common.Paths;
import controllers.common.security.AuthRequired;
import model.user.entities.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
  * Handles API Requests for Socket-Connections to Whiteboards.
  *
  */
public class WhiteboardSessionController extends Controller {
    private static class TicketInformation {
        private User user;
        private Calendar timestamp;
        private long boardId;
    }

    private static Map<String, TicketInformation> tickets = new HashMap<>();
    private static SecureRandom random = new SecureRandom();

    @AuthRequired
    public static Result createTicket(long boardId) {
        //TODO doc
        //TODO test
        //Authenticated User can create ticket for websocket connection.

        TicketInformation ticket = new TicketInformation();
        ticket.user = (User) ctx().args.get("currentuser");
        ticket.boardId = boardId;
        ticket.timestamp = Calendar.getInstance();
        String ticketNumber = new BigInteger(130, random).toString(32);
        tickets.put(ticketNumber, ticket);

        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.SocketPathForWhiteboardId(boardId) + "/" + ticketNumber);

        return created();
    }

    //TODO doc
    public static WebSocket<String> connectToWhiteboard(final long boardId, String ticketNumber) {
        //TODO test

        final TicketInformation requestedTicket = tickets.get(ticketNumber);
        if (requestedTicket == null || requestedTicket.boardId != boardId) {
            //TODO timestamp expiration would be necessary for real system.
            return WebSocket.reject(forbidden());
        }

        tickets.remove(ticketNumber);

        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            @Override
            public Props apply(ActorRef outActor) throws Throwable {
                return Props.create(WebSocketInActor.class, outActor, boardId, requestedTicket.user);
            }
        });
    }
}
