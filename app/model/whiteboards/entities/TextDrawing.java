package model.whiteboards.entities;

import javax.persistence.Entity;

/**
 */
@Entity
public class TextDrawing extends AbstractDrawObject {
    private int x;
    private int y;

    private String text;

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
