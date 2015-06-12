package actors;

import akka.actor.ActorRef;
import model.user.entities.User;

/**
 * A Pair of Websockets (In and Out)
 */
public class AbstractSocketConnection {

    protected ActorRef in;
    protected ActorRef out;

    protected User user;

    public AbstractSocketConnection(ActorRef in, ActorRef out, User user) {
        this.in = in;
        this.out = out;
        this.user = user;
    }

    /**
     * @return websocket in actor - will catch events of the client
     */
    public ActorRef getIn() {
        return in;
    }

    /**
     * @return websocket out actor - will send his messages to the client via server push
     */
    public ActorRef getOut() {
        return out;
    }

    public User getUser() {
        return user;
    }
}
