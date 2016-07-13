package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by Alyssa on 2016-06-17.
 */
public class Paw extends GameObject {
    public static String TAG = "Paw";

    private boolean comingFromTop;
    private boolean extending = true;  // True if paw is currently extending toward the player, false if retracting.
    private static double pawSpeedPx; // Pixels per update for paw to move (vertically).
    private double endYPosPx;     // The y pos at which to start retracting the paw.
    private boolean done = false;       // True when paw has finished extending and retracting.
    private boolean caughtMouse = false;    // Return true if mouse has been caught.

    /* Create a new paw.
     * comingFromTop: True if paw will start going downward from the top, false if upward from bottom.
     * starXPos: The x position to start at.
     * endYPos: The y position at which the paw should start retracting.
     */
    public Paw(Bitmap image, int height, boolean comingFromTop, int startXPos, int endYPos) {
        super(image, height);
        this.comingFromTop = comingFromTop;
        this.pawSpeedPx = Game.convertToPixelY(Game.PAW_SPEED);

        if(comingFromTop)
            this.setOrientation(Game.Orientation.UP);
        int startYPos = comingFromTop? -1*(this.getHeight()) : Game.BASE_HEIGHT;
        super.placeAt(startXPos, startYPos);

        this.extending = true;
        this.done = false;
        this.endYPosPx = Game.convertToPixelY(endYPos);
        this.caughtMouse = false;

    }

    public boolean animationComplete() {
        return done;
    }

    // Assuming animation is incomplete, return true if the cat just got the mouse.
    public boolean caughtMouse() {
        if(caughtMouse) {
            caughtMouse = false;
            return true;
        }
        return false;
    }

    public void update() {
        double newPos = getPxYPos();
        if (comingFromTop) {
            if (extending) {
                newPos = (newPos + pawSpeedPx + getPxHeight() <= endYPosPx)? newPos + pawSpeedPx : endYPosPx - getPxHeight();
                if (newPos == endYPosPx - getPxHeight() && !caughtMouse) {
                    extending = false;
                    caughtMouse = true;
                }
            } else {
                newPos = (newPos - pawSpeedPx + getPxHeight() >= 0)? newPos - pawSpeedPx : -1*getPxHeight();
                if (newPos == -1*getPxHeight()) {
                    done = true;
                }
            }
        } else {
            if (extending) {
                newPos = (newPos - pawSpeedPx >= endYPosPx)? newPos - pawSpeedPx : endYPosPx;
                if (newPos == endYPosPx && !caughtMouse) {
                    extending = false;
                    caughtMouse = true;
                }
            }
            else {
                newPos = (newPos + pawSpeedPx <= GamePanel.screenHeight)? newPos + pawSpeedPx : GamePanel.screenHeight;
                if (newPos == GamePanel.screenHeight) {
                    done = true;
                }
            }
        }
        setY(Game.convertToBaseY(newPos));
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
