package actors.list;

import actors.board.BoardSocketConnection;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.intern.app.AppUserLogoutEvent;
import actors.events.intern.app.ConnectionRejectedEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.BoardUserOnlineSocketEvent;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import model.user.entities.User;
import play.Logger;
import play.libs.Akka;

/**
 * Created by Flo on 12.06.2015.
 */
public class ListSocketInActor extends UntypedActor {

    private ListSocketConnection socketConnection;
    private boolean connectionRejected = false;

    public ListSocketInActor(ActorRef out, User user) {
        Logger.debug("Creating Socket Actor for Whiteboardlist for User " + user.getUsername());
        socketConnection = new ListSocketConnection(self(), out, user);

        //User is Online!
        AppUserLoginEvent loginEvent = new AppUserLoginEvent();
        loginEvent.setUser(user);
        loginEvent.setSocketConnection(socketConnection);
        Akka.system().eventStream().publish(loginEvent);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectionRejectedEvent) {
            connectionRejected = true;
            socketConnection.getOut().tell("rejected", self());
            self().tell(PoisonPill.getInstance(), self());
        }
    }

    @Override
    public void postStop() throws Exception {
        if (connectionRejected) {
            /* connection was rejected - so (although technically connected) this connection was never "online".
             * Thus we don't need to throw offline-events.
             */
            Logger.info(String.format("rejected listsocket-connection for user %s (id=%d)",
                    socketConnection.getUser().getUsername(), socketConnection.getUser().getId()));
            super.postStop();
        } else {
            //User is Offline!
            AppUserLogoutEvent logoutEvent = new AppUserLogoutEvent();

            logoutEvent.setUser(socketConnection.getUser());
            Akka.system().eventStream().publish(logoutEvent);

            super.postStop();
        }
    }
}
