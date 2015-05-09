package actors;

import akka.actor.ActorRef;

/**
 * Created by Flo on 09.05.2015.
 */
public class WebSocketConnection {
    private String boardName;
    private ActorRef in;
    private ActorRef out;

    public WebSocketConnection(String boardName, ActorRef in, ActorRef out) {
        this.boardName = boardName;
        this.in = in;
        this.out = out;
    }

    public String getBoardName() {
        return boardName;
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
}
