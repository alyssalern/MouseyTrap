package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Alyssa on 2016-03-10.
 */
public class Trap extends GameObject {
    private int row;
    private int col;

    public Trap(Bitmap image, int height) {
        super(image, height);
        // setOrientation(orientation);
    }

    public Trap copy() {
        Trap copy = new Trap(this.getBitmap(), this.getHeight());
        copy.placeOnGrid(this.row, this.col);
        return copy;
    }

    /* Place the trap at the given position on the grid.
     * row: The row to place the trap on, starting at 0.
     * col: The column to place the trap on, starting at 0.
     */
    public void placeOnGrid(int row, int col) {
        this.row = row;
        this.col = col;
        double xPos = Game.convertColToBaseX(col);
        xPos += 0.5*((1.0 * Game.WORKING_BASE_WIDTH / Game.COLS) - getWidth());
        double yPos = Game.convertRowToBaseY(row);
        yPos += 0.5*((1.0 * Game.BASE_HEIGHT / Game.ROWS) - getHeight());
        super.placeAt(xPos, yPos);
    }

    public void update() {
        super.update();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
