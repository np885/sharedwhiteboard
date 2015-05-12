package actors;

import actors.events.sockets.BoardUserOpenEvent;
import actors.serialization.SessionEventSerializationUtil;
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
            BoardUserOpenEvent event = (BoardUserOpenEvent) message;
            WebSocketConnection connection = event.getConnection();

            //Tell everyone about the new connection:
            for (WebSocketConnection c : socketConnections) {
                String outputJSON = SessionEventSerializationUtil.serialize(event);
                c.getOut().tell(outputJSON, self());
            }

            //Add connection to list:
            socketConnections.add(connection);
        }
    }

}
