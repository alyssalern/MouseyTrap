package com.alyssalerner.mouseytrap;

import android.graphics.Canvas;

import java.util.ArrayList;

/** Characteristics of one level of the game, which is surpassed by moving the mouse all the way to the right.
 * Created by Alyssa on 2016-03-24.
 */
public class Level {
    private static int nextId = Game.START_LEVEL;  // Next id to assign to a level

    private int id; // This level's id
    private ArrayList<Trap> traps = new ArrayList<>();  // Traps in this level
    private ArrayList<Cheese> cheeses;      // Cheese in this level
    private boolean loading;    // True if loading

    public Level(ArrayList<Trap> traps, ArrayList<Cheese> cheeses) {
        id = nextId++;
        this.traps = traps;
        this.cheeses = cheeses;
        this.loading = false;
    }

    public int getId() {
        return id;
    }

    public void resetIds() {
        nextId = Game.START_LEVEL;
    }

    public ArrayList<Trap> getTraps() {
        return traps;
    }

    public ArrayList<Cheese> getCheese() {
        return cheeses;
    }

    public void removeCheese(Cheese cheese) {
        cheeses.remove(cheese);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    // Set the offset of all components of this level.
    public void setOffset(double offset) {
        for(Trap trap : traps) {
            trap.setOffset(offset);
        }
        for(Cheese cheese : cheeses) {
            if (cheese != null)
                cheese.setOffset(offset);
        }
    }

    // Increment the offset of all components of this level.
    public void offsetBy(double offsetDx) {
        for(Trap trap : traps) {
            trap.offsetBy(offsetDx);
        }

        for(Cheese cheese : cheeses) {
            if (cheese != null)
                cheese.offsetBy(offsetDx);
        }
    }

    public void update() {
        for(Trap trap : traps) {
            trap.update();
        }
        for(Cheese cheese : cheeses) {
            if (cheese != null)
                cheese.update();
        }
    }

    public void draw(Canvas canvas) {
        for(Trap trap : traps) {
            trap.draw(canvas);
        }
        for(Cheese cheese : cheeses) {
            if (cheese != null)
                cheese.draw(canvas);
        }
    }

}
