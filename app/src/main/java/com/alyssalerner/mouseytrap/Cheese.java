package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Random;

/** A piece of cheese which the player can collect.
 * Created by Alyssa on 2016-04-23.
 */
public class Cheese extends CircularGameObject {
    Random rand = new Random();

    public Cheese(Bitmap image, int height) {
        super(image, height);
    }

    public Cheese(Bitmap image, int height, double xPos, double yPos) {
        super(image, height);
        super.placeAt(xPos, yPos);
    }

    public Cheese copy() {
        return new Cheese(this.getBitmap(), this.getHeight(), this.getX(), this.getY());
    }

    public void update() {
        super.update();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    /* Place the cheese at a random position without intersecting other objects or the safe spaces.
     * obstacles: Objects which the cheese should not intersect (will be modified; ensure a copy is passed)
     */
    public void placeAtRandomSpot(ArrayList<GameObject> obstacles) {
        // Increase size of all obstacles so that cheese doesn't go too close to any obstacle
        for(GameObject obstacle : obstacles) {
            obstacle.setX(obstacle.getX() - Game.CHEESE_OBS_DIST);
            obstacle.setY(obstacle.getY() - Game.CHEESE_OBS_DIST);

            obstacle.setWidth(obstacle.getWidth() + (2*Game.CHEESE_OBS_DIST));
            obstacle.setHeight(obstacle.getHeight() + (2*Game.CHEESE_OBS_DIST));
        }

        double xPos, yPos;
        do {
            xPos = Game.SAFE_SPACE_WIDTH + (rand.nextInt(Game.BASE_WIDTH - Game.PLAYER_HEIGHT - (2*Game.SAFE_SPACE_WIDTH)));
            yPos = (rand.nextInt(Game.BASE_HEIGHT - Game.PLAYER_HEIGHT));
            placeAt(xPos, yPos);
        }
        while(collidesWithAny(obstacles));
    }
}
