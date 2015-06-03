package actors.events.socket.boardstate.drawings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SingleLineDrawingDTO extends DrawingDTO {

    @JsonIgnore
    private PointDTO p1;

    @JsonIgnore
    private PointDTO p2;

    @JsonProperty
    public PointDTO[] getPoints() {
        return new PointDTO[] {p1, p2};
    }

    @JsonIgnore
    public void setPoints(int x1, int y1, int x2, int y2) {
        if (p1 == null) {
            p1 = new PointDTO(x1, y1);
        } else {
            p1.setX(x1);
            p1.setY(y1);
        }
        if (p2 == null) {
            p2 = new PointDTO(x2, y2);
        } else {
            p2.setX(x2);
            p2.setY(y2);
        }
    }

    public SingleLineDrawingDTO() {
        super("LineDrawing");
    }
}
