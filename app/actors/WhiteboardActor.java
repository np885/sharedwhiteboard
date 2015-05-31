package actors;

import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.SessionEventSerializationUtil;
import actors.events.socket.boardstate.BoardStateSerializationUtil;
import actors.events.socket.boardstate.Collab;
import actors.events.socket.boardstate.InitialBoardStateEvent;
import actors.events.socket.draw.FreeHandEvent;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import model.whiteboards.entities.AbstractDrawObject;
import model.whiteboards.entities.FreeHandDrawing;
import model.whiteboards.entities.Whiteboard;
import model.whiteboards.repositories.WhiteboardRepo;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardActor extends UntypedActor {
    private WhiteboardRepo whiteboardRepo = new WhiteboardRepo();

    private long boardId;
    private List<WebSocketConnection> socketConnections = new ArrayList<>();

    private Whiteboard currentState;

    public WhiteboardActor(WebSocketConnection connection) {
        Logger.info("Creating Whiteboard Actor: " + self().path());

        this.boardId = connection.getBoardId();

        //load whiteboard state from database:
        currentState = whiteboardRepo.getWhiteboardForId(boardId);

        //add first connection to connections:
        socketConnections.add(connection);

        //tell new connection initial state:
        connection.getOut().tell(produceCurrentStateRepresentation(), self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            onBoardUserOpen((BoardUserOpenEvent) message);
        } else if (message instanceof BoardUserCloseEvent) {
            onBoardUserClosed((BoardUserCloseEvent) message);
        } else if (message instanceof FreeHandEvent) {
            onFreeHandEvent((FreeHandEvent) message);
        }
    }

    private void onBoardUserOpen(BoardUserOpenEvent message) {
        BoardUserOpenEvent event = message;
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

    private void onFreeHandEvent(FreeHandEvent fhe) {
        //save to current state:

        AbstractDrawObject drawObjForElementId = currentState.getDrawObjects().get(fhe.getBoardElementId());
        if (drawObjForElementId == null) {
            drawObjForElementId = new FreeHandDrawing();
            drawObjForElementId.setBoardElementId(fhe.getBoardElementId());
            drawObjForElementId.setWhiteboard(currentState); //<-- this should actually be done by jpa cascade? todo.
            currentState.getDrawObjects().put(fhe.getBoardElementId(), drawObjForElementId);

            ((FreeHandDrawing)drawObjForElementId).getPoints()
                    .add(new FreeHandDrawing.FreeHandDrawingPoint(fhe.getxStart(), fhe.getyStart()));
        } else {
            if (! (drawObjForElementId instanceof FreeHandDrawing)) {
                //error...... todo
            }
        }
        FreeHandDrawing fhd = (FreeHandDrawing) drawObjForElementId;
        fhd.getPoints().add(new FreeHandDrawing.FreeHandDrawingPoint(fhe.getxEnd(), fhe.getyEnd()));

        for (WebSocketConnection c : socketConnections) {
            c.getOut().tell(Json.stringify(Json.toJson(fhe)), self());
        }
    }

    private void onBoardUserClosed(BoardUserCloseEvent message) {
        BoardUserCloseEvent event = message;

        boolean removedConnection = socketConnections.remove(event.getConnection());
        if (!removedConnection) {
            Logger.error("Connection not properly removed!");
        }
        for (WebSocketConnection c : socketConnections) {
            c.getOut().tell(SessionEventSerializationUtil.serialize(event), self());
        }

        //No connections left: persist and kill self:
        if (socketConnections.isEmpty()) {
            persistCurrentState();

            Akka.system().eventStream().publish(new BoardActorClosedEvent(boardId));
            self().tell(PoisonPill.getInstance(), self());
        }
    }

    private void persistCurrentState() {
        currentState = whiteboardRepo.saveWhiteboard(currentState);
    }

    private String produceCurrentStateRepresentation() {
        InitialBoardStateEvent dto = new InitialBoardStateEvent();
        //add collabs to dto:
        for (WebSocketConnection c : socketConnections) {
            dto.getColaborators().add(new Collab(c.getUser().getId(), c.getUser().getUsername()));
        }
        //add drawings etc dto:
        BoardStateSerializationUtil.mapToEvent(currentState, dto);
        return Json.stringify(Json.toJson(dto));
    }
}
