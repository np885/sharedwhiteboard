package actors;

import actors.events.sockets.BoardUserOpenEvent;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import play.Logger;
import play.api.libs.json.Json;
import play.libs.Akka;

public class WebSocketInActor extends UntypedActor {
    private long boardId;
    private ActorRef out;

    public WebSocketInActor(ActorRef out, long boardId) {
        this.boardId = boardId;
        this.out = out;
        BoardUserOpenEvent event = new BoardUserOpenEvent(new WebSocketConnection(boardId, self(), out));
        Akka.system().eventStream().publish(event);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        Logger.debug("SOCKET ACTOR '" + this.toString() + "' WAS KILLED!");
    }


    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("client sendet via socket zum server: " + message);
        out.tell(message, self());
    }

}