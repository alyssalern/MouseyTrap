package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

/** Responsible for playing the losing transition visuals, etc. once the game has lost.
 * Created by Alyssa on 2016-06-15.
 */
public class LostGameTransitioner {
    private static final String TAG = "LostGameTransitioner";
    private enum State {NONE, PAW_ANIMATION, FADING};
    private static Random rand;
    private static Bitmap[] pawImages;
    private static State state;
    private static Paw curPaw;   // Paw being used for this transition.
    private static Player player;

    private static int pawWidth, pawHeight;

    /* Call once during game setup
     * catPaws: A list of bitmaps of all cat paws coming from the bottom.
     */
    public static void initialize(Bitmap[] catPaws) {
        pawImages = catPaws;
        pawWidth = Game.PAW_WIDTH;
        pawHeight = (int)(catPaws[0].getHeight() * (1.0*Game.PAW_WIDTH / catPaws[0].getWidth()));
        rand = new Random();
        state = null;
    }

    /* Begin the transition to game restart, should be called when player has lost.
     * player: The player which the paw will catch.
     * screenHeight: The height of the screen in pixels.
     */
    public static void startTransition(Player thePlayer) {
        player = thePlayer;
        player.setCanMove(false);
        int pawIndex = rand.nextInt(pawImages.length);
        Bitmap pawImage = pawImages[pawIndex];
        boolean comingFromTop = (thePlayer.getY() < Game.BASE_HEIGHT/2);
        int pawStartX = getPawStartX();
        int pawEndY = getPawEndY(comingFromTop);

        curPaw = new Paw(pawImage, pawHeight, comingFromTop, pawStartX, pawEndY);
        state = State.PAW_ANIMATION;
    }

    // Determine if still transitioning.
    public static boolean stillTransitioning() {
        return (state != State.NONE);
    }

    // Find the starting x-position for the paw.
    private static int getPawStartX() {
       int ret = (int)(player.getX() - ((Game.PAW_WIDTH - player.getWidth())/2.0));
        return ret;
    }

    // Find the ending y-position for the paw.
    // comingFromTop: True if paw is coming fromt he top
    private static int getPawEndY(boolean comingFromTop) {
        int pawEndY;
        if (comingFromTop) {
            pawEndY = (int)player.getY() + player.getHeight() + Game.PAW_MOUSE_SEPARATION;
        } else {
            pawEndY = (int)(player.getY() - Game.PAW_MOUSE_SEPARATION);
        }
        return pawEndY;
    }

    public static void update() {
        switch(state) {
            case NONE:  Log.e(TAG, "State is NONE"); break;
            case PAW_ANIMATION:
                if (!curPaw.animationComplete()) {
                    curPaw.update();
                    if(curPaw.caughtMouse()) {
                        player.setVisibility(false);
                    }
                }
                else {
                    state = State.FADING;
                }
                break;
            case FADING:
                state = State.NONE;
                curPaw = null;
                break;
            default: break;
        }
    }

    public static void draw(Canvas canvas) {
        if(curPaw != null)
            curPaw.draw(canvas);
    }

}
