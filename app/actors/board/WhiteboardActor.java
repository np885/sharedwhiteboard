package actors.board;

import actors.events.intern.app.AbstractAppUserEvent;
import actors.events.intern.app.AppUserLoginEvent;
import actors.events.intern.app.AppUserLogoutEvent;
import actors.events.intern.boardsessions.BoardActorClosedEvent;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.boardsessions.SessionEventSerializationUtil;
import actors.events.socket.boardstate.BoardStateSerializationUtil;
import actors.events.socket.boardstate.CollabState;
import actors.events.socket.boardstate.InitialBoardStateEvent;
import actors.events.socket.draw.*;
import actors.events.socket.boardstate.WhiteboardSessionState;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import model.user.entities.User;
import model.whiteboards.entities.*;
import model.whiteboards.repositories.WhiteboardRepo;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;

import java.util.*;

public class WhiteboardActor extends UntypedActor {
    private WhiteboardRepo whiteboardRepo = new WhiteboardRepo();

    private long boardId;
    private List<BoardSocketConnection> socketConnections = new ArrayList<>();

    private Whiteboard currentState;

    //Only available when actor exists
    private WhiteboardSessionState sessionState = new WhiteboardSessionState();

    public WhiteboardActor(BoardSocketConnection connection, List<User> onlineUsers) {
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

        //Init the SessionState of all Collaborators
        sessionState.initCollabStates(currentState, onlineUsers);
        //Change state of current user
        sessionState.changeCollabStateJoin(connectingUser.getId(), true);

        //tell new connection initial state:
        connection.getOut().tell(produceCurrentStateRepresentation(), self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof BoardUserOpenEvent){
            onBoardUserOpen((BoardUserOpenEvent) message);
        } else if (message instanceof BoardUserCloseEvent) {
            onBoardUserClosed((BoardUserCloseEvent) message);
        } else if (message instanceof DrawEvent) {
            onDrawEvent((DrawEvent) message);
        } else if (message instanceof AbstractAppUserEvent) {
            onAppUserEvent((AbstractAppUserEvent) message);
        }
    }

    private void onDrawEvent(DrawEvent drawEvent) {
        if (drawEvent instanceof FreeHandEvent) {
            onFreeHandEvent((FreeHandEvent) drawEvent);
        } else if (drawEvent instanceof SingleLineEvent) {
            onSingleLineEvent((SingleLineEvent) drawEvent);
        } else if (drawEvent instanceof RectangleEvent) {
            onRectangleEvent((RectangleEvent) drawEvent);
        } else if (drawEvent instanceof CircleEvent) {
            onCircleEvent((CircleEvent) drawEvent);
        } else if (drawEvent instanceof TextEvent) {
            onTextEvent((TextEvent) drawEvent);
        } else if (drawEvent instanceof DrawFinishedEvent) {
            onDrawFinishedEvent((DrawFinishedEvent) drawEvent);
        }

        for (BoardSocketConnection c : socketConnections) {
            if ((drawEvent instanceof DrawFinishedEvent) || c.getUser().getId() != drawEvent.getUser().getUserId()) {
                c.getOut().tell(Json.stringify(Json.toJson(drawEvent)), self());
            }
        }
    }

    private void onAppUserEvent(AbstractAppUserEvent event) {
        //Change online Status of Collabs
        if(currentState.getCollaborators().contains(event.getUser())) {
            if (event instanceof AppUserLoginEvent) {
                sessionState.changeCollabStateOnline(event.getUser().getId(), true);
            } else if (event instanceof AppUserLogoutEvent) {
                sessionState.changeCollabState(event.getUser().getId(), false, false);
            }
            //Tell everyone about the online Event
            for (BoardSocketConnection c : socketConnections) {
                String outputJSON = SessionEventSerializationUtil.serializeUserAppEvent(event);
                c.getOut().tell(outputJSON, self());
            }
        }
    }

    private void onDrawFinishedEvent(DrawFinishedEvent drawFinishedEvent) {
        //Adding to sessionLog for initialState
        sessionState.getActivityLog().addFirst(drawFinishedEvent);
    }


    private void onBoardUserOpen(BoardUserOpenEvent userOpenEvent) {
        BoardSocketConnection connection = userOpenEvent.getConnection();

        User connectingUser = userOpenEvent.getConnection().getUser();
        if (! currentState.getCollaborators().contains(connectingUser)) {
            currentState.getCollaborators().add(connectingUser);
            //Adding to sessionState
            sessionState.getCollabs().add(new CollabState(connectingUser.getId(), connectingUser.getUsername()));
            sessionState.changeCollabStateOnline(connectingUser.getId(), true);
        }
        //Changing state
        sessionState.changeCollabStateJoin(connectingUser.getId(), true);

        //Tell everyone about the new connection:
        for (BoardSocketConnection c : socketConnections) {
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
    }


    private void onTextEvent(TextEvent txtEv) {
        AbstractDrawObject drawObjForElementId = currentState.getDrawObjects().get(txtEv.getBoardElementId());
        if (drawObjForElementId == null) {
            //new line:
            drawObjForElementId = initDrawObjectAndAddToState(new TextDrawing(), txtEv);
        }
        TextDrawing txtDrawing = (TextDrawing) drawObjForElementId;
        txtDrawing.setX(txtEv.getX());
        txtDrawing.setY(txtEv.getY());
        txtDrawing.setText(txtEv.getText());
    }

    private void onRectangleEvent(RectangleEvent re) {
        AbstractDrawObject drawObjForElementId = currentState.getDrawObjects().get(re.getBoardElementId());
        if (drawObjForElementId == null) {
            //new rectangle:
            drawObjForElementId = initDrawObjectAndAddToState(new RectangleDrawing(), re);
        }
        RectangleDrawing rectDrawing = (RectangleDrawing) drawObjForElementId;
        rectDrawing.setX(re.getxStart());
        rectDrawing.setY(re.getyStart());
        rectDrawing.setWidth(re.getWidth());
        rectDrawing.setHeight(re.getHeight());
        rectDrawing.normalize();
        re.normalize();
    }

    private void onCircleEvent(CircleEvent ce) {
        AbstractDrawObject drawObjForElementId = currentState.getDrawObjects().get(ce.getBoardElementId());
        if (drawObjForElementId == null) {
            //new circle:
            drawObjForElementId = initDrawObjectAndAddToState(new CircleDrawing(), ce);
        }
        CircleDrawing rectDrawing = (CircleDrawing) drawObjForElementId;
        rectDrawing.setCenterX(ce.getCenterX());
        rectDrawing.setCenterY(ce.getCenterY());
        rectDrawing.setRadius(ce.getRadius());
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
        for (BoardSocketConnection c : socketConnections) {
            c.getOut().tell(SessionEventSerializationUtil.serialize(event), self());
        }
        //Change join State of Collab
        sessionState.changeCollabStateJoin(event.getConnection().getUser().getId(), false);

        //No connections left: persist and kill self:
        if (socketConnections.isEmpty()) {
            Akka.system().eventStream().publish(new BoardActorClosedEvent(boardId));
            self().tell(PoisonPill.getInstance(), self());
        }
    }

    @Override
    public void postStop() throws Exception {
        persistCurrentState();
        super.postStop();
    }

    private void persistCurrentState() {
        currentState = whiteboardRepo.saveWhiteboard(currentState);
    }

    private String produceCurrentStateRepresentation() {
        InitialBoardStateEvent dto = new InitialBoardStateEvent();
        //add drawings etc dto:
        BoardStateSerializationUtil.mapDrawingsToEvent(currentState, dto);
        //Add complete activityLog
        dto.getActivityLog().addAll(sessionState.getActivityLog());
        //Add CollabState from Session
        dto.getColaborators().addAll(sessionState.getCollabs());
        return Json.stringify(Json.toJson(dto));
    }
}
