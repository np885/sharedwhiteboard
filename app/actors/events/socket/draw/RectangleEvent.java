package actors.events.socket.draw;

/**
 */
public class RectangleEvent extends DrawEvent {
    private int xStart;
    private int yStart;
    private int width;
    private int height;

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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String getEventType() {
        return "RectangleEvent";
    }

//    this.eventType = 'RectangleEvent';
//    this.boardElementId = boardElementId;
//    this.xStart = xStart;
//    this.yStart = yStart;
//    this.width = xEnd;
//    this.height = yEnd;
}
