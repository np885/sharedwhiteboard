package actors;

import actors.board.WhiteboardActor;
import actors.events.intern.app.AbstractAppUserEvent;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.intern.app.AppUserLogoutEvent;
import actors.events.intern.app.ConnectionRejectedEvent;
import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.AbstractBoardSessionEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.SessionEventSerializationUtil;
import actors.events.socket.boardstate.SimpleUser;
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
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationActor extends UntypedActor {
    private static class UserOnlineData {
        private ListSocketConnection connection;
        private Long currentlyJoined;
        public UserOnlineData(ListSocketConnection connection, Long currentlyJoined) {
            this.connection = connection;
            this.currentlyJoined = currentlyJoined;
        }
    }

    public static final String NAME = "Aplication";

    private static ApplicationActor instance;

    /* maps <boardId, BoardActor> */
    private Map<Long, ActorRef> boardActors = new HashMap<>();

    /* maps <User, <ListSocketConnection, currentlyJoined>>*/
    private Map<User, UserOnlineData> listSocketConnections = new ConcurrentHashMap<>();



    /* todo: the whole online user thing is legacy, now that we have the
     * listSocketConnections, this should be the source for the online data. At the moment
     * we have much redundancy in the 'users online management' */
    private List<User> onlineUser = new ArrayList<>();

    public ApplicationActor() {
        instance = this;
        Akka.system().eventStream().subscribe(self(), AbstractBoardSessionEvent.class);
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
                listSocketConnections.get(u).connection.getOut().tell(Json.stringify(Json.toJson(lsce)), self());
            }
        }
    }

    private void onAppUserEvent(AbstractAppUserEvent event) {
        if(event instanceof AppUserLoginEvent){
            UserOnlineData userOnlineData = listSocketConnections.get(event.getUser());
            if (userOnlineData != null) {
                Logger.warn("Double login for user: " + event.getUser());
                //todo... double login. kill old login the hard way?
                ((AppUserLoginEvent) event).getSocketConnection().getIn().tell(new ConnectionRejectedEvent(), self());
                return;
            }
            listSocketConnections.put(event.getUser(),
                    new UserOnlineData(((AppUserLoginEvent) event).getSocketConnection(), null));

            Logger.debug(event.getUser().getUsername() + " is Online!");
            onlineUser.add(event.getUser());
        } else if (event instanceof AppUserLogoutEvent) {
            UserOnlineData removed = listSocketConnections.remove(event.getUser());
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
            //logout event means, the client is (parallel to our server actions here) closing its sockets.
            //sending the logout event to the outlogging client can eventually cause closedChannelExceptions.
            if (!(event instanceof AppUserLogoutEvent && u.getId().equals(event.getUser().getId()))) {
                listSocketConnections.get(u).connection.getOut().tell(onOffSocketEvent, self());
            }
        }
    }

    /**
     * A User opened a Whiteboard and thus created a new Websocket Connection:
     */
    private void onBoardUserOpenEvent(BoardUserOpenEvent event) {
        UserOnlineData userOnlineData = listSocketConnections.get(event.getConnection().getUser());
        if (userOnlineData == null) {
            Logger.warn("Caught Board Open Event for a User, who is not logged in! userid="
                    + ((event.getConnection().getUser() == null)
                    ? "user is null." : event.getConnection().getUser().getId()));
        } else {
            if (userOnlineData.currentlyJoined != null) {
                Logger.warn("Caught Board Open Event for a user, who was still joined to another Board!" +
                        " userid=" + event.getConnection().getUser().getId() +
                        ", newly joined boardid=" + event.getBoardId() +
                        ", old joined board: " + userOnlineData.currentlyJoined);
                Logger.warn("Old join state will be overwritten.");
            }
            userOnlineData.currentlyJoined = event.getBoardId();
            ListStateChangedEvent lscEvent = new ListStateChangedEvent();
            lscEvent.setUser(new SimpleUser(event.getConnection().getUser()));
            lscEvent.setStructuralChanges(false);
            Akka.system().eventStream().publish(lscEvent);
        }

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
        //Track live data for reactive eye on whiteboardlist:
        UserOnlineData userOnlineData = listSocketConnections.get(buce.getConnection().getUser());
        if (userOnlineData == null) {
            Logger.warn("Caught Board Closed Event for a User, who is not logged in! userid="
                    + ((buce.getConnection().getUser() == null)
                        ? "user is null." : buce.getConnection().getUser().getId()));
        } else {
            if (userOnlineData.currentlyJoined == buce.getBoardId()) {
                userOnlineData.currentlyJoined = null;
                ListStateChangedEvent lscEvent = new ListStateChangedEvent();
                lscEvent.setUser(new SimpleUser(buce.getConnection().getUser()));
                lscEvent.setStructuralChanges(false);
                Akka.system().eventStream().publish(lscEvent);
            } else {
                Logger.warn("Caught Board Closed Event for a board, for which the user in the event" +
                        " is not joined! userid=" + buce.getConnection().getUser().getId() +
                        ", boardid=" + buce.getBoardId());
            }
        }

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


    /**
     * @return Map &lt;Online User, Board he is currently joined (may be null, if not joined any board)&gt;
     */
    public static Map<User, Long> getOnlineList() {
        Map<User, Long> result = new HashMap<>();

        for (User u : instance.listSocketConnections.keySet()) {
            result.put(u, instance.listSocketConnections.get(u).currentlyJoined);
        }

        return result;
    }

}
