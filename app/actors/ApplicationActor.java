package actors;

import actors.events.intern.app.AbstractAppUserEvent;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.intern.app.AppUserLogoutEvent;
import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.BoardSessionEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import play.Logger;
import play.libs.Akka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationActor extends UntypedActor {

    public static final String NAME = "Aplication";

    /* maps <boardId, BoardActor> */
    private Map<Long, ActorRef> boardActors = new HashMap<>();
    private List<User> onlineUser = new ArrayList<>();

    public ApplicationActor() {
        Akka.system().eventStream().subscribe(self(), BoardSessionEvent.class);
        Akka.system().eventStream().subscribe(self(), BoardActorClosedEvent.class);
        Akka.system().eventStream().subscribe(self(), AbstractAppUserEvent.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            onBoardUserOpenEvent((BoardUserOpenEvent) message);
        } else if (message instanceof BoardActorClosedEvent) {
            onBoardActorClosedEvent((BoardActorClosedEvent) message);
        } else if (message instanceof AbstractAppUserEvent) {
            onAppUserEvent((AbstractAppUserEvent) message);
        }
    }

    private void onAppUserEvent(AbstractAppUserEvent event) {
        if(event instanceof AppUserLoginEvent){
            onlineUser.add(event.getUser());
        } else if (event instanceof AppUserLogoutEvent) {
            onlineUser.remove(event.getUser());
        }
        for(long boardId : boardActors.keySet()){
            boardActors.get(boardId).tell(event, self());
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
                    Props.create(WhiteboardActor.class, event.getConnection(), onlineUser),
                    "whiteboards-" + event.getBoardId());
            boardActors.put(event.getBoardId(), actorRef);
        } else {
            ActorRef actorRef = boardActors.get(event.getBoardId());
            actorRef.tell(event, self());
        }
    }


}
