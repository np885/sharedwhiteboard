package actors.events.socket.boardstate;

import actors.events.socket.boardstate.drawings.*;
import model.user.entities.User;
import model.whiteboards.entities.*;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BoardStateSerializationUtil {

    public static List<CollabState> mapToCollabState(Whiteboard whiteboard){
        List<CollabState> collabStateList = new ArrayList<>();
        for(User user : whiteboard.getCollaborators()){
            collabStateList.add(mapToCollabState(user));
        }
        return collabStateList;
    }

    public static CollabState mapToCollabState(User user){
        return new CollabState(user.getId(), user.getUsername());
    }

    public static void mapDrawingsToEvent(Whiteboard entity, InitialBoardStateEvent event) {
        //drawings:
        for (int drawObjectId : entity.getDrawObjects().keySet()) {
            AbstractDrawObject drawObject = entity.getDrawObjects().get(drawObjectId);
            event.getDrawings().add(mapAbstractDrawing(drawObject));
        }
    }

    private static DrawingDTO mapAbstractDrawing(AbstractDrawObject drawObject) {
        if (drawObject instanceof FreeHandDrawing) {
            return mapAbstractDrawing((FreeHandDrawing) drawObject);
        } else if (drawObject instanceof SingleLineDrawing) {
            return mapDrawing((SingleLineDrawing) drawObject);
        } else if (drawObject instanceof RectangleDrawing) {
            return mapDrawing((RectangleDrawing) drawObject);
        } else if (drawObject instanceof CircleDrawing) {
            return mapDrawing((CircleDrawing) drawObject);
        } else if (drawObject instanceof TextDrawing) {
            return mapDrawing((TextDrawing) drawObject);
        }
        throw new IllegalArgumentException("No a mappable drawobject.");
    }

    private static DrawingDTO mapDrawing(TextDrawing td) {
        TextDrawingDTO dto = new TextDrawingDTO(td.getX(), td.getY(), td.getText());
        dto.setBoardElementId(td.getBoardElementId());
        return dto;
    }

    private static DrawingDTO mapDrawing(CircleDrawing cd) {
        CircleDrawingDTO dto = new CircleDrawingDTO(cd.getCenterX(), cd.getCenterY(), cd.getRadius());
        dto.setBoardElementId(cd.getBoardElementId());
        return dto;
    }

    private static DrawingDTO mapDrawing(RectangleDrawing rd) {
        RectangleDrawingDTO dto = new RectangleDrawingDTO(rd.getX(), rd.getY(), rd.getWidth(), rd.getHeight());
        dto.setBoardElementId(rd.getBoardElementId());
        return dto;
    }


    private static DrawingDTO mapAbstractDrawing(FreeHandDrawing fhd) {
        FreeHandDrawingDTO dto = new FreeHandDrawingDTO();
        dto.setBoardElementId(fhd.getBoardElementId());

        for (FreeHandDrawing.FreeHandDrawingPoint point : fhd.getPoints()) {
            dto.getPoints().add(new PointDTO(point.getX(), point.getY()));
        }

        return dto;
    }


    private static DrawingDTO mapDrawing(SingleLineDrawing sld) {
        SingleLineDrawingDTO dto = new SingleLineDrawingDTO();

        dto.setBoardElementId(sld.getBoardElementId());

        dto.setPoints(sld.getX1(), sld.getY1(), sld.getX2(), sld.getY2());

        return dto;
    }

}
