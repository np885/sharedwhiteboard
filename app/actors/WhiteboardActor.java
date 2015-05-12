package actors;

import actors.events.sockets.BoardUserOpenEvent;
import akka.actor.UntypedActor;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardActor extends UntypedActor {

    private long boardId;
    private List<WebSocketConnection> socketConnections = new ArrayList<>();

    public WhiteboardActor( WebSocketConnection connection) {
        Logger.debug("Creating Whiteboard Actor: " + self().path());

        this.boardId = connection.getBoardId();
        socketConnections.add(connection);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            WebSocketConnection connection = ((BoardUserOpenEvent) message).getConnection();
            socketConnections.add(connection);
        }
    }

}
