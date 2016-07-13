package com.alyssalerner.mouseytrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/** Responsible for storing information on lives, etc. as well as displaying that information.
 * Created by Alyssa on 2016-04-26.
 */
public class Overlay {
    private static final String TAG = "Overlay";
    private Paint scoreTextPaint;   // Paint to use while drawing score.
    private Paint highScoreTextPaint;
    private int score = 0;
    private int highScore;
    private Timer timer;
    private Bitmap cheeseImage;
    private int cheeseYPos;
    private int scorePosX, scorePosY;
    private int highScorePosX, highScorePosY;

    public Overlay(GamePanel gamePanel, Context context) {
        Log.i(TAG, "Creating overlay...");
        initializeScoreText();
        initializeHighScoreText();

        cheeseImage = Bitmap.createScaledBitmap(GamePanel.cheeseImage, Game.SCORE_TEXT_SIZE, Game.SCORE_TEXT_SIZE, false);
        cheeseYPos = (int)Game.convertToPixelY(Game.BASE_HEIGHT - scorePosX - cheeseImage.getHeight() - 15);

        ScoreKeeper.initialize(gamePanel, context);
        highScore = ScoreKeeper.getPlayerHighScore();

        timer = new Timer(GamePanel.timerImage);
        Log.i(TAG, "...Finished creating overlay.");
    }

    // Reset this overlay to how it should be at the start of a new game
    public void reset() {
        score = 0;
        highScore = ScoreKeeper.getPlayerHighScore();
        timer.reset();
    }

    public Timer getTimer() {
        return timer;
    }

    // Add the given amount of time to the timer.
    public void addToTimer(double time) {
        timer.addTime(time);
    }

    // Add points to the score.
    public void addToScore(int points) {
        this.score += points;
        if(ScoreKeeper.updateHighScore(score)) {
            highScore = score;
        }
    }

    // Update the high score to current score if it's greater.
    public void updateHighScore() {
        ScoreKeeper.updateHighScore(score);
    }

    // Return true if timer has run out
    public boolean outOfTime() {
        return (timer.getTimeLeft() <= 0);
    }

    public void draw(Canvas canvas) {
        // canvas.drawBitmap(cheeseImage, scorePosX, cheeseYPos, null);
        canvas.drawText("" + score, scorePosX, scorePosY, scoreTextPaint);
        canvas.drawText("" + highScore, highScorePosX, highScorePosY, highScoreTextPaint);
        timer.draw(canvas);
    }

    public void update() {
        timer.update();
    }

    // Initialize appearance of score text.
    private void initializeScoreText() {
        scorePosX = (int)Game.convertToPixelX(Game.SCORE_POS_X);
        scorePosY = (int)Game.convertToPixelY(Game.SCORE_POS_Y);

        scoreTextPaint = new Paint();
        scoreTextPaint.setColor(Color.rgb(40, 40, 40));
        scoreTextPaint.setTextSize((float)Game.convertToPixelY(Game.SCORE_TEXT_SIZE));

        cheeseImage = Bitmap.createScaledBitmap(GamePanel.cheeseImage, Game.SCORE_TEXT_SIZE, Game.SCORE_TEXT_SIZE, false);
        cheeseYPos = (int)Game.convertToPixelY(Game.BASE_HEIGHT - scorePosX - cheeseImage.getHeight() - 15);
    }

    private void initializeHighScoreText() {
        highScorePosX = (int)Game.convertToPixelX(Game.HIGHSCORE_POS_X);
        highScorePosY = (int)Game.convertToPixelY(Game.HIGHSCORE_POS_Y);

        highScoreTextPaint = new Paint();
        highScoreTextPaint.setColor(Color.rgb(40, 40, 40));
        highScoreTextPaint.setTextSize((float)Game.convertToPixelY(Game.HIGHSCORE_TEXT_SIZE));
    }
}
