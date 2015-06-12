package actors.list;

import actors.board.BoardSocketConnection;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.intern.app.AppUserLogoutEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.BoardUserOnlineSocketEvent;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import model.user.entities.User;
import play.libs.Akka;

/**
 * Created by Flo on 12.06.2015.
 */
public class ListSocketInActor extends UntypedActor {

    private ListSocketConnection socketConnection;

    public ListSocketInActor(ActorRef out, User user) {
        socketConnection = new ListSocketConnection(self(), out, user);

        //User is Online!
        AppUserLoginEvent loginEvent = new AppUserLoginEvent();
        loginEvent.setUser(user);
        loginEvent.setSocketConnection(socketConnection);
        Akka.system().eventStream().publish(loginEvent);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        //probably nothing to do here..?
    }

    @Override
    public void postStop() throws Exception {
        //User is Offline!
        AppUserLogoutEvent logoutEvent = new AppUserLogoutEvent();

        logoutEvent.setUser(socketConnection.getUser());
        Akka.system().eventStream().publish(logoutEvent);

        super.postStop();
    }
}
