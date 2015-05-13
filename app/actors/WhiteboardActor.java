package actors;

import actors.events.sockets.BoardUserOpenEvent;
import actors.serialization.boardsessions.SessionEventSerializationUtil;
import actors.serialization.boardstate.Collab;
import actors.serialization.boardstate.InitialBoardStateEventDTO;
import akka.actor.UntypedActor;
import play.Logger;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardActor extends UntypedActor {
    private long boardId;
    private List<WebSocketConnection> socketConnections = new ArrayList<>();

    public WhiteboardActor(WebSocketConnection connection) {
        Logger.debug("Creating Whiteboard Actor: " + self().path());

        this.boardId = connection.getBoardId();

        //add to connections:
        socketConnections.add(connection);

        //tell new connection initial state:
        connection.getOut().tell(produceCurrentStateRepresentation(), self());
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

            //tell the new connection the initial State:
            connection.getOut().tell(produceCurrentStateRepresentation(), self());
        }
    }

    private String produceCurrentStateRepresentation() {
        InitialBoardStateEventDTO dto = new InitialBoardStateEventDTO();
        for (WebSocketConnection c : socketConnections) {
            dto.getColaborators().add(new Collab(c.getUser().getId(), c.getUser().getUsername()));
        }
        return Json.stringify(Json.toJson(dto));
    }


}
