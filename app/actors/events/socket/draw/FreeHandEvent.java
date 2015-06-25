package actors.events.socket.draw;

/**
 */
public class FreeHandEvent extends AbstractDrawEvent {
    private int xStart;
    private int yStart;

    private int xEnd;
    private int yEnd;

    public int getxStart() {
        return xStart;
    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getyStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getxEnd() {
        return xEnd;
    }

    public void setxEnd(int xEnd) {
        this.xEnd = xEnd;
    }

    public int getyEnd() {
        return yEnd;
    }

    public void setyEnd(int yEnd) {
        this.yEnd = yEnd;
    }

    @Override
    public String getEventType() {
        return "FreeHandEvent";
    }
}
