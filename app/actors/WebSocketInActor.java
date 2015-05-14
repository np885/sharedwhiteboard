package actors;

import actors.events.SWBEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.SocketEvent;
import actors.events.socket.draw.FreeHandEvent;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import model.user.entities.User;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;

public class WebSocketInActor extends UntypedActor {
    private final long boardId;
    private final ActorRef out;
    private final WebSocketConnection socketConnection;

    private ActorRef boardActorRef;

    public WebSocketInActor(ActorRef out, long boardId, User user) {
        this.boardId = boardId;
        this.out = out;

        this.socketConnection = new WebSocketConnection(boardId, user, self(), out);
        BoardUserOpenEvent event = new BoardUserOpenEvent(socketConnection);
        Akka.system().eventStream().publish(event);
    }

    @Override
    public void postStop() throws Exception {
        Logger.debug("SOCKET ACTOR '" + this.toString() + "' WAS KILLED!");

        tellMyWhiteboardActor(new BoardUserCloseEvent(this.socketConnection));
        super.postStop();
    }




    @Override
    public void onReceive(Object message) throws Exception {
        Logger.debug("client via socket -> server: " + message);
        JsonNode parsedMessage = Json.parse((String) message);
        String eventType = parsedMessage.get("eventType").asText();
        switch (eventType) {
            case "FreeHandEvent":
                FreeHandEvent freeHandEvent = Json.fromJson(parsedMessage, FreeHandEvent.class);
                tellMyWhiteboardActor(freeHandEvent);
                break;
        }
    }


    private void tellMyWhiteboardActor(SWBEvent event) {
         if (boardActorRef == null) {
             boardActorRef = Akka.system().actorFor("user/whiteboards-" + boardId);
         }

        boardActorRef.tell(event, self());
    }

}