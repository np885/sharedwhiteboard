package model.whiteboards.entities;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
public class FreeHandDrawing extends AbstractDrawObject {
    public static class FreeHandDrawingPoint implements Serializable {
        private static final long serialVersionUID = 1001L;
        public FreeHandDrawingPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "FreeHandDrawingPoint{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    //will be persisted as blobs.
    @Lob
    private LinkedList<FreeHandDrawingPoint> points = new LinkedList<>();

    public LinkedList<FreeHandDrawingPoint> getPoints() {
        return points;
    }
}
