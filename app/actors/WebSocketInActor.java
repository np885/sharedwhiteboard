package actors;

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
    private long boardId;
    private ActorRef out;

    public WebSocketInActor(ActorRef out, long boardId, User user) {
        this.boardId = boardId;
        this.out = out;

        BoardUserOpenEvent event = new BoardUserOpenEvent(new WebSocketConnection(boardId, user, self(), out));
        Akka.system().eventStream().publish(event);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        Logger.debug("SOCKET ACTOR '" + this.toString() + "' WAS KILLED!");
    }




    @Override
    public void onReceive(Object message) throws Exception {
        Logger.debug("client via socket -> server: " + message);
        JsonNode parsedMessage = Json.parse((String) message);
        String eventType = parsedMessage.get("eventType").asText();
        switch (eventType) {
            case "FreeHandEvent":
                FreeHandEvent freeHandEvent = Json.fromJson(parsedMessage, FreeHandEvent.class);
                ActorRef boardActorRef = Akka.system().actorFor("user/whiteboards-" + boardId);

                if (!boardActorRef.isTerminated()) {
                    boardActorRef.tell(freeHandEvent, self());
                }
                break;
        }
    }

    public static void main(String[] args) {
        System.out.println(Json.toJson(new FreeHandEvent()));
    }

}