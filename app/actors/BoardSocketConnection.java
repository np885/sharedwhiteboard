package actors;

import akka.actor.ActorRef;
import model.user.entities.User;

/**
 * Created by Flo on 09.05.2015.
 */
public class BoardSocketConnection extends AbstractSocketConnection {
    private final long boardId;
    public BoardSocketConnection(long boardId, User user, ActorRef in, ActorRef out) {
        super(in, out, user);
        this.boardId = boardId;
    }

    public long getBoardId() {
        return boardId;
    }
}
