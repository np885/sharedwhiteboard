package actors.events.socket.boardstate.drawings;

/**
 * Created by Flo on 31.05.2015.
 */
public class PointDTO {
    private int x;
    private int y;

    public PointDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }

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
}
