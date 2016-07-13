package com.alyssalerner.mouseytrap;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Field;

public class Game extends AppCompatActivity {

    public enum Orientation {UP, DOWN, LEFT, RIGHT}
    public static final int FPS = 44;   // FPS of game

    public final static int BASE_WIDTH = 2560;     // Assumed width of screen before converting
    public final static int BASE_HEIGHT = 1440;    // Assumed height of screen before

    public final static int ROWS = 6;
    public final static int COLS = 8;

    // Player variables
    public final static int PL_LOWEST_SPEED = 2;   // Lowest possible player speed in pixels/update (based on GamePanel.BASE_HEIGHT)
    public final static int PL_HIGHEST_SPEED = 102;   // Highest possible player speed in pixels/update (based on GamePanel.BASE_HEIGHT)
    public final static int PL_MAX_SPEED = 100;     // The number to correlate with maximum speed (min is always 0)
    public final static double PL_SPEED_RATIO = 1.0;   // The horizontal to vertical speed ratio. Eg. if a speed of 10 correlates to 30 pixels/update in the y direction, then a speed of 10 will correlate to 30*SPEED_RATIO pixels/update in the x direction.
    public final static int PL_START_SPEED_X = 60;
    public final static int PL_START_SPEED_Y = 60;

    public final static String backgroundImage = "wood_a";
    public final static String playerImage = "mouse_circle";
    public final static String trapImage = "mousetrap_b";
    public final static String cheeseImage = "cheese_wedge";
    public final static String timerImage = "cheese_whole";

    public final static int PLAYER_HEIGHT = BASE_HEIGHT / (ROWS * 2) + 5;
    public final static int TRAP_HEIGHT = BASE_HEIGHT / (ROWS * 2);
    public static final int SAFE_SPACE_WIDTH = (BASE_WIDTH - (TRAP_HEIGHT * COLS * 2)) / 2;
    public final static int SLIDE_THRESHOLD = BASE_WIDTH - SAFE_SPACE_WIDTH + (PLAYER_HEIGHT / 2); // Start sliding when player moves past here
    public static final int WORKING_BASE_WIDTH = BASE_WIDTH - (SAFE_SPACE_WIDTH * 2);
    public static final int START_LEVEL = 1;    // Level to start on (for debugging)

    // Cheese values
    public static final int CHEESE_TIME_VALUE = 150;    // *160*Amount to add to timer when pick up
    public final static int CHEESE_HEIGHT = PLAYER_HEIGHT + 35;
    public static final int CHEESE_OBS_DIST = 100;    // Distance a cheese can be from traps and other cheeses

    // Overlay values
    public static final int SCORE_POS_X = 70;
    public static final int SCORE_POS_Y = BASE_HEIGHT - SCORE_POS_X;
    public static final int HIGHSCORE_POS_X = SCORE_POS_X;
    public static final int HIGHSCORE_POS_Y = 110;
    public static final int SCORE_TEXT_SIZE = 80;
    public static final int HIGHSCORE_TEXT_SIZE = SCORE_TEXT_SIZE;
    public static final int TIMER_HEIGHT = 150;
    public static final int TIMER_X_POS = BASE_WIDTH - TIMER_HEIGHT - 50;
    public static final int TIMER_Y_POS = 50;
    public static final int TOTAL_TIME = 300;  // *300*The total amount of 'time' on the timer (1 = an update)
    public static final int WARNING_THRESHOLD = TOTAL_TIME / 4;    // Time left before time warning begins

    // Transitioner values
    public static final int N_PAWS = 2; // Number of cat paw images to choose from.
    public static final int PAW_WIDTH = BASE_WIDTH / 4;
    public static final int PAW_SPEED = 60;  // base y-pixels per update to move the paw.
    public static final int PAW_MOUSE_SEPARATION = 240;  // Vertical pixels between the mouse and the top of the paw when grabbing mouse.

    public final static int PAN_SPEED = 120;   // Speed at which view will pan, in GamePanel.baseWidth / update.

    private GamePanel gamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        gamePanel = new GamePanel(this);
        setContentView(gamePanel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScoreKeeper.saveHighScore();
    }

    /* Find the pixel coord of a standard x coord.
         * baseX: The x coordinate relative to BASE_WIDTH
         */
    public static double convertToPixelX(double baseX) {
        return baseX * (1.0 * GamePanel.screenWidth / BASE_WIDTH);
    }

    /* Find the pixel coord of a standard y coord.
    * baseY: The x coordinate relative to BASE_WIDTH
    */
    public static double convertToPixelY(double baseY) {
        return baseY * (1.0 * GamePanel.screenHeight / BASE_HEIGHT);
    }

    /* Find the base coord of a pixel x coord.
     * pixelX: The pixel x coordinate or width.
     */
    public static double convertToBaseX(double pixelX) {
        return pixelX * (1.0 * BASE_WIDTH / GamePanel.screenWidth);
    }

    /* Find the base coord of a pixel x coord.
    * pixelX: The pixel x coordinate or width.
    */
    public static double convertToBaseY(double pixelY) {
        return pixelY * (1.0 * BASE_HEIGHT / GamePanel.screenHeight);
    }

    // Convert a col to its corresponding base pixel.
    public static double convertColToBaseX(int col) {
        return SAFE_SPACE_WIDTH + (col * WORKING_BASE_WIDTH / COLS);
    }

    // Convert a row to its corresponding base pixel.
    public static double convertRowToBaseY(int row) {
        return 1.0 * row * BASE_HEIGHT / ROWS;
    }


    /* Find resource id from a string representation.
     * resName: The string name of the resource.
     *
     * Taken from http://stackoverflow.com/questions/4427608/android-getting-resource-id-from-string
     */
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
