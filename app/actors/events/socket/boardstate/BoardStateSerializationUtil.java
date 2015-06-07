package actors.events.socket.boardstate;

import actors.events.socket.boardstate.drawings.DrawingDTO;
import actors.events.socket.boardstate.drawings.FreeHandDrawingDTO;
import actors.events.socket.boardstate.drawings.PointDTO;
import actors.events.socket.boardstate.drawings.SingleLineDrawingDTO;
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
        }
        throw new IllegalArgumentException("No a mappable drawobject.");
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
