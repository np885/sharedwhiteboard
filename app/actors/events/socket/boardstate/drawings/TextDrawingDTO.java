package actors.events.socket.boardstate.drawings;

/**
 */
public class TextDrawingDTO extends DrawingDTO {
    private int x;
    private int y;
    private String text;

    public TextDrawingDTO(int x, int y, String text) {
        super("TextDrawing");
        this.x = x;
        this.y = y;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
