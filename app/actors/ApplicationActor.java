package actors;

import actors.board.WhiteboardActor;
import actors.events.intern.app.AbstractAppUserEvent;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.intern.app.AppUserLogoutEvent;
import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.BoardSessionEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.SessionEventSerializationUtil;
import actors.events.socket.liststate.ListStateChangedEvent;
import actors.list.ListSocketConnection;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import model.user.entities.User;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;

import java.util.*;

public class ApplicationActor extends UntypedActor {

    public static final String NAME = "Aplication";

    private static ApplicationActor instance;

    /* maps <boardId, BoardActor> */
    private Map<Long, ActorRef> boardActors = new HashMap<>();

    /* maps <User, ListSocketConnection>*/
    private Map<User, ListSocketConnection> listSocketConnections = new HashMap<>();

    /* todo: the whole online user thing is legacy, now that we have the
     * listSocketConnections, this should be the source for the online data. At the moment
     * we have much redundancy in the 'users online management' */
    private List<User> onlineUser = new ArrayList<>();

    public ApplicationActor() {
        instance = this;
        Akka.system().eventStream().subscribe(self(), BoardSessionEvent.class);
        Akka.system().eventStream().subscribe(self(), BoardActorClosedEvent.class);
        Akka.system().eventStream().subscribe(self(), AbstractAppUserEvent.class);
        Akka.system().eventStream().subscribe(self(), ListStateChangedEvent.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            onBoardUserOpenEvent((BoardUserOpenEvent) message);
        } else if (message instanceof BoardUserCloseEvent) {
            onBoardUserCloseEvent((BoardUserCloseEvent) message);
        } else if (message instanceof BoardActorClosedEvent) {
            onBoardActorClosedEvent((BoardActorClosedEvent) message);
        } else if (message instanceof AbstractAppUserEvent) {
            onAppUserEvent((AbstractAppUserEvent) message);
        } else if (message instanceof ListStateChangedEvent) {
            onListStateChangedEvent((ListStateChangedEvent) message);
        }
    }


    private void onListStateChangedEvent(ListStateChangedEvent lsce) {
        for (User u : listSocketConnections.keySet()) {
            if (u.getId() != lsce.getUser().getUserId()) {
                listSocketConnections.get(u).getOut().tell(Json.stringify(Json.toJson(lsce)), self());
            }
        }
    }

    private void onAppUserEvent(AbstractAppUserEvent event) {
        if(event instanceof AppUserLoginEvent){
            ListSocketConnection connection = listSocketConnections.get(event.getUser());
            if (connection != null) {
                Logger.warn("Double login for user: " + event.getUser());
                //todo... double login. kill old login the hard way?
            }
            listSocketConnections.put(event.getUser(), ((AppUserLoginEvent) event).getSocketConnection());

            Logger.debug(event.getUser().getUsername() + " is Online!");
            onlineUser.add(event.getUser());
        } else if (event instanceof AppUserLogoutEvent) {
            ListSocketConnection removed = listSocketConnections.remove(event.getUser());
            if (removed == null) {
                Logger.warn("Could not remove listSocketConnection for User " + event.getUser().getId()
                        + ", probably because he was not logged in! Check why a logout event appears for a user" +
                        " who is not logged in!");
            }
            Logger.debug(event.getUser().getUsername() + " is Offline!");
            onlineUser.remove(event.getUser());
        }
        for(long boardId : boardActors.keySet()){
            boardActors.get(boardId).tell(event, self());
        }

        String onOffSocketEvent = SessionEventSerializationUtil.serializeUserAppEvent(event);
        for (User u : listSocketConnections.keySet()) {
            listSocketConnections.get(u).getOut().tell(onOffSocketEvent, self());
        }
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

    private void onBoardUserCloseEvent(BoardUserCloseEvent buce) {
        if(!boardActors.containsKey(buce.getBoardId())){
            Logger.warn("Caught Board Closed Event to a board, which has no board-actor!");
        } else {
            ActorRef actorRef = boardActors.get(buce.getBoardId());
            actorRef.tell(buce, self());
        }
    }

    private void onBoardActorClosedEvent(BoardActorClosedEvent message) {
        Logger.debug("Removed closed BoardActor with id=" + message.getBoardId());
        boardActors.remove(message.getBoardId());
    }


    public static Set<User> getOnlineList() {
        return new HashSet<>(instance.listSocketConnections.keySet());
    }

}
