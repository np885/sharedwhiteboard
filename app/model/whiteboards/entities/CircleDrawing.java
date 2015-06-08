package model.whiteboards.entities;

import javax.persistence.Entity;

/**
 */
@Entity
public class CircleDrawing extends AbstractDrawObject {
    private int centerX;
    private int centerY;
    private int radius;

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
