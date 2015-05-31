package actors.events.socket.boardstate.drawings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FreeHandDrawingDTO extends DrawingDTO {

    @JsonProperty
    private List<PointDTO> points = new ArrayList<>();

    public FreeHandDrawingDTO() {
        super("FreeHandDrawing");
    }

    public List<PointDTO> getPoints() {
        return points;
    }
}
