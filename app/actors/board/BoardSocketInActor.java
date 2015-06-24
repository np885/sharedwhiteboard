package actors.board;

import actors.events.SWBEvent;
import actors.events.socket.boardstate.SimpleUser;
import actors.events.intern.boardsessions.BoardUserCloseEvent;
import actors.events.intern.boardsessions.BoardUserOpenEvent;
import actors.events.socket.draw.*;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import model.user.entities.User;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;

import java.util.Date;

public class BoardSocketInActor extends UntypedActor {
    private final long boardId;
    private final ActorRef out;
    private final BoardSocketConnection socketConnection;

    private ActorRef boardActorRef;

    public BoardSocketInActor(ActorRef out, long boardId, User user) {
        this.boardId = boardId;
        this.out = out;

        this.socketConnection = new BoardSocketConnection(boardId, user, self(), out);
        BoardUserOpenEvent event = new BoardUserOpenEvent(socketConnection);
        Akka.system().eventStream().publish(event);
    }

    @Override
    public void postStop() throws Exception {
        Logger.debug("SOCKET ACTOR '" + this.toString() + "' WAS KILLED!");

        Akka.system().eventStream().publish(new BoardUserCloseEvent(this.socketConnection));
        super.postStop();
    }




    @Override
    public void onReceive(Object message) throws Exception {
        String eventType;
        JsonNode parsedMessage;
        try {
            Logger.debug("client via socket -> server: " + message);
            parsedMessage = Json.parse((String) message);
            eventType = parsedMessage.get("eventType").asText();
        } catch (Exception e) {
            Logger.error("Error while parsing Board-Event: " + message.toString() + "\n... event will be ignored:", e);
            return;
        }

        switch (eventType) {
            case "FreeHandEvent":
                FreeHandEvent freeHandEvent = Json.fromJson(parsedMessage, FreeHandEvent.class);
                //Adding User that Draws Line
                freeHandEvent.setUser(new SimpleUser(socketConnection.getUser().getId(), socketConnection.getUser().getUsername()));
                tellMyWhiteboardActor(freeHandEvent);
                break;
            case "LineEvent":
                SingleLineEvent lineEvent = Json.fromJson(parsedMessage, SingleLineEvent.class);
                //Adding User that Draws Line
                lineEvent.setUser(new SimpleUser(socketConnection.getUser().getId(), socketConnection.getUser().getUsername()));
                tellMyWhiteboardActor(lineEvent);
                break;
            case "RectangleEvent":
                RectangleEvent rectangleEvent = Json.fromJson(parsedMessage, RectangleEvent.class);
                //Adding User that Draws Line
                rectangleEvent.setUser(new SimpleUser(socketConnection.getUser().getId(), socketConnection.getUser().getUsername()));
                tellMyWhiteboardActor(rectangleEvent);
                break;
            case "CircleEvent":
                CircleEvent circleEvent = Json.fromJson(parsedMessage, CircleEvent.class);
                //Adding User that Draws Line
                circleEvent.setUser(new SimpleUser(socketConnection.getUser().getId(), socketConnection.getUser().getUsername()));
                tellMyWhiteboardActor(circleEvent);
                break;
            case "TextEvent":
                TextEvent te = Json.fromJson(parsedMessage, TextEvent.class);
                te.setUser(new SimpleUser(socketConnection.getUser().getId(), socketConnection.getUser().getUsername()));
                tellMyWhiteboardActor(te);
                break;
            case "DrawFinishEvent":
                DrawFinishedEvent drawFinishedEvent = Json.fromJson(parsedMessage, DrawFinishedEvent.class);
                drawFinishedEvent.setUser(new SimpleUser(socketConnection.getUser().getId(), socketConnection.getUser().getUsername()));
                drawFinishedEvent.setLogDate(new Date());
                tellMyWhiteboardActor(drawFinishedEvent);
        }
    }


    private void tellMyWhiteboardActor(SWBEvent event) {
         if (boardActorRef == null) {
             boardActorRef = Akka.system().actorFor("user/whiteboards-" + boardId);
         }

        boardActorRef.tell(event, self());
    }

}