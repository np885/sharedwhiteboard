package actors.events.socket.boardstate;

import actors.events.socket.boardstate.drawings.DrawingDTO;
import actors.events.socket.boardstate.drawings.FreeHandDrawingDTO;
import actors.events.socket.boardstate.drawings.PointDTO;
import model.whiteboards.entities.AbstractDrawObject;
import model.whiteboards.entities.FreeHandDrawing;
import model.whiteboards.entities.RectangleDrawing;
import model.whiteboards.entities.Whiteboard;

/**
 */
public class BoardStateSerializationUtil {
    public static void mapToEvent(Whiteboard entity, InitialBoardStateEvent event) {
        //drawings:
        for (int drawObjectId : entity.getDrawObjects().keySet()) {
            AbstractDrawObject drawObject = entity.getDrawObjects().get(drawObjectId);
            event.getDrawings().add(mapDrawing(drawObject));
        }
        //...
    }

    private static DrawingDTO mapDrawing(AbstractDrawObject drawObject) {
        if (drawObject instanceof FreeHandDrawing) {
            return mapDrawing((FreeHandDrawing)drawObject);
        } //else if (drawObject instanceof ...)
        throw new IllegalArgumentException("No a mappable drawobject.");
    }


    private static DrawingDTO mapDrawing(FreeHandDrawing fhd) {
        FreeHandDrawingDTO dto = new FreeHandDrawingDTO();

        for (FreeHandDrawing.FreeHandDrawingPoint point : fhd.getPoints()) {
            dto.getPoints().add(new PointDTO(point.getX(), point.getY()));
        }

        return dto;
    }
}
