package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/** Responsible for the screen's background.
 * Created by Alyssa on 2016-03-06.
 */
public class Background {

    private Bitmap image;    // The full background image, of correct size.

    private int pxHeight;   // Height in pixels of the background image
    private int pxWidth;    // Width in pixels of the background image

    private double offset;        // The x coordinate (<= 0) to start the background at.
    private double pxOffset;      // offset in pixels
    private int repeats;       // Number of times the background needs to be drawn.

    /* Create a new background for a level.
     * image: The main image to display in the background.
     */
    public Background(Bitmap imageOrig) {

        this.pxHeight = GamePanel.screenHeight;
        this.pxWidth = (int)(1.0*pxHeight * imageOrig.getWidth() / imageOrig.getHeight());
        this.image = Bitmap.createScaledBitmap(imageOrig, pxWidth, pxHeight, false);
        repeats = findRepeats();
    }

    /* Set the background's offset.
     * offset: The offset (rel to BASE_WIDTH), can be positive or negative.
     */
    public void setOffset(double offset) {
        if(offset != 0)
            this.offset = (offset % GamePanel.screenWidth) - GamePanel.screenWidth;
        else this.offset = 0;

        pxOffset = Game.convertToPixelX(this.offset);
    }

    /* Increment the current offset by the given amount.
     * offsetDx: The amount to increment by (rel to BASE_WIDTH).
     */
    public void offsetBy(double offsetDx) {
        setOffset(offset + offsetDx);
    }

    /* Determine the number of times this image must be repeated.
     */
    private int findRepeats() {
        int repeats = 1;
        double posOffset = pxOffset + pxWidth;
        while(posOffset < GamePanel.screenWidth) {
            repeats++;
            posOffset += pxWidth;
        }
        return repeats;
    }

    public void update() {
        repeats = findRepeats();
    }

    public void draw(Canvas canvas) {
        System.out.println("Background repeats: " + repeats);
        for(int i = 0; i < repeats; i++) {
            canvas.drawBitmap(image, (int)pxOffset + (i*pxWidth), 0, null);
        }
    }
}
