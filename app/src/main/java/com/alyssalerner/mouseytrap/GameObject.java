package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

/** Contains shared characteristics of all spacial 'objects' on the board
 * Created by Alyssa on 2016-03-06.
 */
public abstract class GameObject {
    private static final String TAG = "GameObject";
    private Game.Orientation orientation;    // Object's current orientation.

    private double xPos, yPos;      // Coordinates of object
    private double pxXPos, pxYPos;  // Coordinates of object in pixels.
    private int width, height;      // Size of object (rel to GamePanel.BASE_HEIGHT, BASE_WIDTH)
    private int pxWidth, pxHeight;  // SIze of object in pixels
    private double offset;             // x position to draw this object relative to its real coordinates.
    private double pxOffset;           // x offset of this game object in pixels.
    private boolean visible = true; // True to draw this object.

    private Bitmap image;       // Image that represents the character.
    private Bitmap[] imagesRotated = new Bitmap[4]; // Contains all rotated versions of the character as [UP, DOWN, LEFT, RIGHT}


    /* image: A bitmap of the character, which can be of arbitrary dimensions
              Expected that provided bitmap contains a character that is facing downward.
     * height: The height of this image (relative to BASE_HEIGHT)
     */
    public GameObject(Bitmap image, int height) {
        this.height = height;
        width = (int)(1.0*image.getWidth() * height / image.getHeight());
        orientation = Game.Orientation.DOWN;  // DOWN is the expected default orientation of the image
        this.image = image;
        updateSize();
        updatePxCoords();

        // Pre-fetch all rotated images of object
        Matrix matrix = new Matrix();
        imagesRotated[1] = Bitmap.createBitmap(this.image);
        matrix.postRotate(90);
        imagesRotated[2] = Bitmap.createBitmap(this.image, 0, 0, pxWidth, pxHeight, matrix, true);
        matrix.postRotate(90);
        imagesRotated[0] = Bitmap.createBitmap(this.image, 0, 0, pxWidth, pxHeight, matrix, true);
        matrix.postRotate(90);
        imagesRotated[3] = Bitmap.createBitmap(this.image, 0, 0, pxWidth, pxHeight, matrix, true);
    }

    public double getX() {
        return xPos;
    }

    public void setX(double xPos) {
        this.xPos = xPos;
        updatePxCoords();
    }

    public double getY() {
        return yPos;
    }

    public double getPxYPos() {
        return pxYPos;
    }

    public void setY(double yPos) {
        this.yPos = yPos;
        updatePxCoords();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        updateSize();
    }

    public int getHeight() {
        return height;
    }

    public int getPxHeight() {
        return pxHeight;
    }

    public void setHeight(int height) {
        this.height = height;
        updateSize();
    }

    // Set the offset of this game object.
    public void setOffset(double offset) {
        this.offset = offset;
        pxOffset = Game.convertToPixelX(offset);
    }

    // Increment the offset of this game object.
    public void offsetBy(double offsetDx) {
        setOffset(offset + offsetDx);
    }

    /* Set the visibility of this game object.
     * visible: True to set to visible, false to set to invisible.
     */
    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public Game.Orientation getOrientation() {
        return orientation;
    }

    // Place player at the given position on the screen.
    public void placeAt(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        updatePxCoords();
    }

    public Rect getRectangle() {
        return new Rect((int)xPos, (int)yPos, (int)xPos + width, (int)yPos + height);
    }

    public Bitmap getBitmap() {
        return image;
    }

    // Flip the character's bitmap to face the given Orientation.
    public void setOrientation(Game.Orientation orientation) {
        switch(orientation) {
            case UP:    image = imagesRotated[0];   break;
            case DOWN:  image = imagesRotated[1];   break;
            case LEFT:    image = imagesRotated[2];   break;
            case RIGHT:  image = imagesRotated[3];   break;
            default:    break;
        }
        this.orientation = orientation;
        pxWidth = image.getWidth();
        pxHeight = image.getHeight();
    }

    /* Determine if this character collides with another
     * The other object.
     */
    public boolean collidesWith(GameObject other) {
        return Rect.intersects(this.getRectangle(), other.getRectangle());
    }

    /* Determine if this object collides with any of the objects given
     * others: The other game objects
     */
    public boolean collidesWithAny(ArrayList<GameObject> others) {
        for(GameObject other : others) {
            if(this.collidesWith(other)) {
                return true;
            }
        }
        return false;
    }

    public double getOffset() {
        return offset;
    }


    public void update() {

    }

    // Draw the game object
    public void draw(Canvas canvas) {
        if(visible)
            canvas.drawBitmap(image, (int)(pxXPos + pxOffset), (int)pxYPos, null);
    }

    // Update size of bitmap image to appropriate values.
    private void updateSize() {
        pxWidth = (int)Game.convertToPixelX(width);
        pxHeight = (int)Game.convertToPixelY(height);
        image = Bitmap.createScaledBitmap(image, pxWidth, pxHeight, false);
    }

    // Update stored pixel coordinates to appropriate values.
    private void updatePxCoords() {
        this.pxXPos = Game.convertToPixelX(xPos);
        this.pxYPos = Game.convertToPixelY(yPos);
    }
}
