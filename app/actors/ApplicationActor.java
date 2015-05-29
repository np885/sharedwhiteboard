package actors;

import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.BoardSessionEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
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
        Akka.system().eventStream().subscribe(self(), BoardActorClosedEvent.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            onBoardUserOpenEvent((BoardUserOpenEvent) message);
        } else if (message instanceof BoardActorClosedEvent) {
            onBoardActorClosedEvent((BoardActorClosedEvent) message);
        }
    }

    private void onBoardActorClosedEvent(BoardActorClosedEvent message) {
        Logger.debug("Removed closed BoardActor with id=" + message.getBoardId());
        boardActors.remove(message.getBoardId());
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
