package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/** The player controlled by the user.
 * Created by Alyssa on 2016-03-06.
 */
public class Player extends CircularGameObject {

    private double speedX, speedY;           // Speed the player should be at while it is moving (persists even while stopped)
    private double velocityX, velocityY;     // Velocity that the player is actually moving at this moment
    private double pxVelocityX, pxVelocityY;   // Velocity in pixels
    private boolean canMove = true;

    private double startXPos;          // Starting x position for player
    private double startYPos;          // Starting y position for player

    public Player(Bitmap image, int height) {
        super(image, height);

        startXPos = (Game.SAFE_SPACE_WIDTH - getWidth()) / 2.0;
        startYPos = (Game.BASE_HEIGHT - getHeight()) / 2.0;

        speedX = Game.PL_START_SPEED_X;
        speedY = Game.PL_START_SPEED_Y;
        setOrientation(Game.Orientation.UP);
        velocityY = -1*speedY;
        pxVelocityX = getXPixelsPerUpdate();
        pxVelocityY = getYPixelsPerUpdate();
    }

    /* Place player at the starting position of a level.
     * vertical: False if only want to position player horizontally.
     */
    public void positionAtStart(boolean vertical) {
        if(canMove) {
            setX(startXPos);
            if (vertical)
                setY(startYPos);
        }
    }

    // Return true if moving down
    public boolean getMovingDown() {
        return velocityY > 0;
    }

    /* Begin moving in given direction, at speed determined by speed
     * down: true if move down, false if move up
     * right: true if move right, false if move left
     */
    public void moveInDirection(boolean right, boolean down) {
        if(canMove) {
            velocityX = right ? speedX : -1 * speedX;
            velocityY = down ? speedY : -1 * speedY;
            pxVelocityX = getXPixelsPerUpdate();
            pxVelocityY = getYPixelsPerUpdate();
        }
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
        if(!canMove) {
            stopHorizontalMovement();
            velocityY = 0;
            pxVelocityY = 0;
        }
        else
            setVelocity(0, speedY);
    }

    public void stopHorizontalMovement() {
        velocityX = 0;
        pxVelocityX = 0;
    }


    public boolean canMove() {
        return canMove;
    }

    private void setVelocity(double velX, double velY) {
        if(canMove) {
            velocityX = velX;
            velocityY = velY;
            pxVelocityX = getXPixelsPerUpdate();
            pxVelocityY = getYPixelsPerUpdate();
        }
    }

    private double getXPixelsPerUpdate() {
        double pixelsPerSpeedLevel = (1.0*Game.PL_HIGHEST_SPEED - Game.PL_LOWEST_SPEED) / Game.PL_MAX_SPEED;
        double pixelsPerUpdate = pixelsPerSpeedLevel * velocityX;
        return pixelsPerUpdate * Game.PL_SPEED_RATIO;
    }

    private double getYPixelsPerUpdate() {
        double pixelsPerSpeedLevel = (1.0*Game.PL_HIGHEST_SPEED - Game.PL_LOWEST_SPEED) / Game.PL_MAX_SPEED;
        double pixelsPerUpdate = pixelsPerSpeedLevel * velocityY;
        return pixelsPerUpdate;
    }

    // Update player's position
    public void update() {
        super.update();

        // Bounce off top wall
        if(getY() <= 0 && velocityY < 0) {
            setVelocity(velocityX, -1*velocityY);
            setOrientation(Game.Orientation.DOWN);
        }
        // Bounce off bottom wall
        else if (getY() >= Game.BASE_HEIGHT - getHeight() && velocityY >= 0) {
            setVelocity(velocityX, -1*velocityY);
            setOrientation(Game.Orientation.UP);
        }
        // Hit left wall
        if(getX() <= 0 && velocityX < 0) {
            setVelocity(0, velocityY);
        }
        // Bounce off right wall
        else if (getX() >= Game.BASE_WIDTH - getWidth() && velocityX >= 0) {
            setVelocity(Game.BASE_WIDTH - getWidth(), velocityY);
        }
        // Move by amount dictated by velocity
        setX(getX() + pxVelocityX);
        setY(getY() + pxVelocityY);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

}
