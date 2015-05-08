package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class WebSocketInActor extends UntypedActor {
    private String boardName;

    public WebSocketInActor(ActorRef out, String boardName) {
        out.tell("whooop whooop, tis is the sound of teh police", self());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

//        ActorRef eventDispatcher = Akka.system().actorFor(BoardEventDispatcher.path);
//        eventDispatcher.tell(new SocketClosedEvent(self(), out, boardName), self());
    }


    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("client sendet via socket zum server: " + message);
//        JsonNode parsed = Json.parse((String) message);
//        int x = parsed.get("x").asInt();
//        int y = parsed.get("y").asInt();
//        Akka.system().actorFor("/user/" + boardName).tell(new DrawEvent(x, y), self());
    }

}