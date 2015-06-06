package actors;

import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.SessionEventSerializationUtil;
import actors.events.socket.boardstate.BoardStateSerializationUtil;
import actors.events.socket.boardstate.Collab;
import actors.events.socket.boardstate.InitialBoardStateEvent;
import actors.events.socket.draw.DrawEvent;
import actors.events.socket.draw.FreeHandEvent;
import actors.events.socket.draw.SingleLineEvent;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import model.user.entities.User;
import model.whiteboards.entities.AbstractDrawObject;
import model.whiteboards.entities.FreeHandDrawing;
import model.whiteboards.entities.SingleLineDrawing;
import model.whiteboards.entities.Whiteboard;
import model.whiteboards.repositories.WhiteboardRepo;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        //add to collabs if not already done:
        User connectingUser = connection.getUser();
        if (! currentState.getCollaborators().contains(connectingUser)) {
            currentState.getCollaborators().add(connectingUser);
        }

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
        } else if (message instanceof SingleLineEvent) {
            onSingleLineEvent((SingleLineEvent) message);
        }
    }


    private void onBoardUserOpen(BoardUserOpenEvent userOpenEvent) {
        WebSocketConnection connection = userOpenEvent.getConnection();

        User connectingUser = userOpenEvent.getConnection().getUser();
        if (! currentState.getCollaborators().contains(connectingUser)) {
            currentState.getCollaborators().add(connectingUser);
        }

        //Tell everyone about the new connection:
        for (WebSocketConnection c : socketConnections) {
            String outputJSON = SessionEventSerializationUtil.serialize(userOpenEvent);
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
            drawObjForElementId = initDrawObjectAndAddToState(new FreeHandDrawing(), fhe);
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

    private void onSingleLineEvent(SingleLineEvent sle) {
        AbstractDrawObject drawObjForElementId = currentState.getDrawObjects().get(sle.getBoardElementId());
        if (drawObjForElementId == null) {
            //new line:
            drawObjForElementId = initDrawObjectAndAddToState(new SingleLineDrawing(), sle);
        }
        SingleLineDrawing slDrawing = (SingleLineDrawing) drawObjForElementId;
        slDrawing.setX1(sle.getxStart());
        slDrawing.setY1(sle.getyStart());
        slDrawing.setX2(sle.getxEnd());
        slDrawing.setY2(sle.getyEnd());

        for (WebSocketConnection c : socketConnections) {
            c.getOut().tell(Json.stringify(Json.toJson(sle)), self());
        }
    }

    private AbstractDrawObject initDrawObjectAndAddToState(AbstractDrawObject drawObject, DrawEvent event) {
        drawObject.setBoardElementId(event.getBoardElementId());
        drawObject.setWhiteboard(currentState);
        currentState.getDrawObjects().put(event.getBoardElementId(), drawObject);

        return drawObject;
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
        //add drawings etc dto:
        BoardStateSerializationUtil.mapToEvent(currentState, dto);
        //add online status to collabs:
        Set<Long> joinedIds = new HashSet<>();
        for (WebSocketConnection c : socketConnections) {
            joinedIds.add(c.getUser().getId());
        }
        for (Collab c : dto.getColaborators()) {
            if (joinedIds.contains(c.getUserId())) {
                c.setJoined(true);
            }
        }

        return Json.stringify(Json.toJson(dto));
    }
}
