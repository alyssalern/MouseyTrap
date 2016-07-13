package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;


/**
 * Created by Alyssa on 2016-05-06.
 */
public class Timer {
    private static final String TAG = "Timer";
    private static final double X_POS = Game.TIMER_X_POS;
    private static final double Y_POS = Game.TIMER_Y_POS;
    private static final int HEIGHT = Game.TIMER_HEIGHT;
    private static final int DULL_VALUE = 70;   // How dull to make the used parts of the timer image.

    private Bitmap workingImage;    // The current image of the timer
    private Bitmap fullImage;       // The full image of full color
    private int pxHeight;   // Width and height of timer in pixels
    private double pxXPos, pxYPos;

    private int timeLeft = Game.TOTAL_TIME;    // The value left in the timer, out of a total of FULL_VALUE
    private int[] fullPixels;
    private int[] workingPixels;

    public Timer(Bitmap image) {
        pxHeight = (int)Game.convertToPixelX(HEIGHT);
        pxXPos = Game.convertToPixelX(X_POS);
        pxYPos = Game.convertToPixelY(Y_POS);
        initBitmaps(image);
        reset();
    }

    // Initialize the bitmaps
    private void initBitmaps(Bitmap image) {
        fullImage = Bitmap.createScaledBitmap(image, pxHeight, pxHeight, false);
        fullPixels = new int[pxHeight * pxHeight];
        workingPixels = new int[pxHeight * pxHeight];
        fullImage.getPixels(fullPixels, 0, pxHeight, 0, 0, pxHeight, pxHeight);
        workingImage = Bitmap.createBitmap(fullImage);
        workingImage.getPixels(workingPixels, 0, pxHeight, 0, 0, pxHeight, pxHeight);
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    // Add the given time to the timer (won't go past TOTAL_TIME)
    public void addTime(double time) {
        timeLeft += time;
        if(timeLeft > Game.TOTAL_TIME)
            timeLeft = Game.TOTAL_TIME;

        int xPixel, yPixel;
        for(int i = 0; i < fullPixels.length; i++) {
            xPixel = (i % pxHeight);
            yPixel = (i / pxHeight);

            // Take back full color when necessary
            if(Color.alpha(fullPixels[i]) > 0 && !isDull(xPixel, yPixel)) {
                workingPixels[i] = fullPixels[i];
            }
        }
    }

    public void reset() {
        timeLeft = Game.TOTAL_TIME;
        for (int i = 0; i < fullPixels.length; i++) {
            workingPixels[i] = fullPixels[i];
        }
    }

    // Decrement the timer each update
    public void update() {
        timeLeft--;
    }

    public void draw(Canvas canvas) {
        workingImage = fullImage.copy(Bitmap.Config.ARGB_8888, true);
        int xPixel, yPixel;

        // Consider each pixel at a time
        for(int i = 0; i < fullPixels.length; i++) {
            xPixel = (i % pxHeight);
            yPixel = (i / pxHeight);

            if(Color.alpha(fullPixels[i]) > 0) {
                if (isDull(xPixel, yPixel)) {
                    workingPixels[i] = makeDull(fullPixels[i]);

                    if (timeLeft <= Game.WARNING_THRESHOLD) {
                        workingPixels[i] = Color.rgb(170, 40, 40);
                    }
                }
            }
        }

        workingImage.setPixels(workingPixels, 0, pxHeight, 0, 0, pxHeight, pxHeight);
        canvas.drawBitmap(workingImage, (int)pxXPos, (int)pxYPos, null);
    }

    /* Determine whether a given pixel on the current timer should be dull or not.
     * x: The pixel's x position on the bitmap.
     * y: The pixel's y position on the bitmap.
     */
    private boolean isDull(double x, double y) {
        // Place circle's middle at origin for easier calculation
        x -= pxHeight/2.0;
        y -= pxHeight/2.0;

        // Find the quadrant of this pixel (where positive coords are bottom right)
        int quadrant = 1;
        if(x >= 0 && y >= 0)         quadrant = 2;
        else if(x < 0 && y >= 0)     quadrant = 3;
        else if(x < 0 && y < 0)    quadrant = 4;

        x = Math.abs(x);
        y = Math.abs(y);

        // Calculate the portion around the circle that this pixel is located.
        double radiansPastQuadrant = (quadrant % 2 == 0)? Math.atan(y/x) : Math.atan(x/y);
        double pixelPortion = 0.25 * (quadrant - 1);
        pixelPortion += radiansPastQuadrant / (2*Math.PI);

        double timerPortion = 1.0 - (1.0*timeLeft / Game.TOTAL_TIME);
        return pixelPortion < timerPortion;
    }

    /* Retrieve a duller version of the given color.
     * color: The original (brighter) color
     */
    private int makeDull(int color) {
        int red = Color.red(color) - DULL_VALUE;
        int green = Color.green(color) - DULL_VALUE;
        int blue = Color.blue(color) - DULL_VALUE;

        if(red < 0) red = 0;
        if(green < 0) green = 0;
        if(blue < 0) blue = 0;

        return Color.rgb(red, green, blue);
    }
}
