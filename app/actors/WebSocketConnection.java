package actors;

import akka.actor.ActorRef;
import model.user.entities.User;

/**
 * Created by Flo on 09.05.2015.
 */
public class WebSocketConnection {
    private long boardId;
    private User user;
    private ActorRef in;
    private ActorRef out;

    public WebSocketConnection(long boardId, User user, ActorRef in, ActorRef out) {
        this.boardId = boardId;
        this.user = user;
        this.in = in;
        this.out = out;
    }

    public long getBoardId() {
        return boardId;
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
