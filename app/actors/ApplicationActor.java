package actors;

import actors.events.sockets.BoardSessionEvent;
import actors.events.sockets.BoardUserOpenEvent;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import javassist.tools.web.Webserver;
import play.Logger;
import play.libs.Akka;

import java.util.HashMap;
import java.util.Map;

public class ApplicationActor extends UntypedActor {

    public static final String NAME = "Aplication";

    /* maps <boardId, BoardActor> */
    private Map<Long, ActorRef> boardActors = new HashMap<>();

    public ApplicationActor() {
        Akka.system().eventStream().subscribe(self(), BoardSessionEvent.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            onBoardUserOpenEvent((BoardUserOpenEvent) message);
        }
    }

    /**
     * A User opened a Whiteboard and thus created a new Websocket Connection:
     */
    private void onBoardUserOpenEvent(BoardUserOpenEvent event) {
        if(!boardActors.containsKey(event.getBoardId())){
            ActorRef actorRef = Akka.system().actorOf(
                    Props.create(WhiteboardActor.class, event.getConnection()),
                    "whiteboards-" + event.getBoardId());
            boardActors.put(event.getBoardId(), actorRef);
        } else {
            ActorRef actorRef = boardActors.get(event.getBoardId());
            actorRef.tell(event, self());
        }
    }


}
