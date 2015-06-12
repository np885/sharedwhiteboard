package actors.list;

import actors.AbstractSocketConnection;
import akka.actor.ActorRef;
import model.user.entities.User;

/**
 */
public class ListSocketConnection extends AbstractSocketConnection {

    public ListSocketConnection(ActorRef in, ActorRef out, User user) {
        super(in, out, user);
    }
}
