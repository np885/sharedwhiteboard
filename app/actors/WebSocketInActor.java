package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import play.Logger;
import play.api.libs.json.Json;

public class WebSocketInActor extends UntypedActor {
    private String boardName;
    private ActorRef out;

    public WebSocketInActor(ActorRef out, String boardName) {
        this.out = out;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        Logger.debug("SOCKET ACTOR '" + this.toString() + "' WAS KILLED!");

//        ActorRef eventDispatcher = Akka.system().actorFor(BoardEventDispatcher.path);
//        eventDispatcher.tell(new SocketClosedEvent(self(), out, boardName), self());
    }


    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("client sendet via socket zum server: " + message);
        out.tell(message, self());
//        JsonNode parsed = Json.parse((String) message);
//        int x = parsed.get("x").asInt();
//        int y = parsed.get("y").asInt();
//        Akka.system().actorFor("/user/" + boardName).tell(new DrawEvent(x, y), self());
    }

}