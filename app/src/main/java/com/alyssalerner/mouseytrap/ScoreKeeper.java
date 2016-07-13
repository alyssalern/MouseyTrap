package com.alyssalerner.mouseytrap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/** Retrieves and keeps high score information.
 * Created by Alyssa on 2016-06-24.
 */
public class ScoreKeeper {
    private static final String TAG = "ScoreKeeper";
    private static int playerHighScore; // Highest score on this device.
    private static boolean highScoreChanged = false;    // True if high score has been changed since program opened.
    private static GamePanel gamePanel;
    private static SharedPreferences preferences;

    public static void initialize(GamePanel panel, Context context) {
        gamePanel = panel;
        preferences = context.getSharedPreferences("highScore", Context.MODE_PRIVATE);
        playerHighScore = retrieveHighScore(context);
    }

    // Save the high score to the file if it's changed.
    public static void saveHighScore() {
        if(highScoreChanged) {
            preferences.edit().putInt("highScore", playerHighScore).apply();
            highScoreChanged = true;
        }
}

    /* Update the player's high score to the new value if it's greater.
     * newScore: The new score which the current one will be compared with.
     * return true if the high score was updated to the new value.
     */
    public static boolean updateHighScore(int newScore) {
        if (newScore > playerHighScore) {
            playerHighScore = newScore;
            highScoreChanged = true;
            return true;
        }
        return false;
    }

    public static int getPlayerHighScore() {
        return playerHighScore;
    }

    // Retrieve the player's high score from the file.
    private static int retrieveHighScore(Context context) {

        int storedHighScore = preferences.getInt("highScore", 0);

        playerHighScore = storedHighScore;
        return playerHighScore;
    }
}
