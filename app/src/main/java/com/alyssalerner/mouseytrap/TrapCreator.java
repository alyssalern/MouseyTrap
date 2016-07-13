package com.alyssalerner.mouseytrap;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/** Responsible for creating and locating traps such that they're all possible
 * to surpass, among other rules.
 * Created by Alyssa on 2016-05-24.
 */
public class TrapCreator {
    private static final String TAG = "TrapCreator";
    private static final int MAX_PATH_STEPS = 20;   // Max number of steps the path through the traps can have.
    private static final int MAX_TRAPS = (Game.ROWS * Game.COLS) - MAX_PATH_STEPS;

    /* Represents the status of a space on the final 'board'.
     * TRAP_ALLOWED:    Space is empty and a trap is allowed here in the future (default).
     * TRAP_DISALLOWED: Space is empty and no trap can be placed here.
     * HAS_TRAP:        Occupied by a trap.
     */
    private enum SpaceStatus {TRAP_ALLOWED, TRAP_DISALLOWED, HAS_TRAP};
    private enum HorizDir {STRAIGHT, RIGHT};
    private static int straightSteps;   // Number of consecutive straight steps so far.
    private static int rightSteps;  // Number of consecutive right steps so far.
    private static Random rand = new Random();

    private static SpaceStatus[][] spaces = new SpaceStatus[Game.ROWS][Game.COLS];  // The statuses of all the spaces on the board at the current time.;

    // For creating a path
    private static int curRow, curCol;
    private static boolean curVertDir;
    private static HorizDir curHorizDir;

    /* Create and locate all the traps for the given level.
     * levelId: The id of the level to create traps for.
     */
    public static ArrayList<Trap> createTraps(int levelId) {
        createDefinitePath();
        int nTraps = getNTraps(levelId);
        int row, col;

        // Create traps one at a time
        if (nTraps < (int)(MAX_TRAPS * 0.75)) {
            for (int i = 0; i < nTraps; i++) {
                do {
                    row = rand.nextInt(Game.ROWS);
                    col = rand.nextInt(Game.COLS);
                }
                while (spaces[row][col] != SpaceStatus.TRAP_ALLOWED);
                spaces[row][col] = SpaceStatus.HAS_TRAP;
            }
        }
        // Alternatively, create empty spaces one at a time when more efficient.
        else {
            int curNTraps = 0;
            for (int i = 0; i < Game.ROWS; i++) {
                for(int j = 0; j < Game.ROWS; j++) {
                    if(spaces[i][j] == SpaceStatus.TRAP_ALLOWED)
                        spaces[i][j] = SpaceStatus.HAS_TRAP;
                    curNTraps++;
                }
            }

            while(curNTraps > nTraps) {
                do {
                    row = rand.nextInt(Game.ROWS);
                    col = rand.nextInt(Game.COLS);
                }
                while (spaces[row][col] == SpaceStatus.TRAP_DISALLOWED);
                spaces[row][col] = SpaceStatus.TRAP_ALLOWED;
                curNTraps--;
            }
        }

        straightSteps = 0;
        rightSteps = 0;
        ArrayList<Trap> finalTraps = createFinalTraps();
        // printPath(false);
        return finalTraps;
    }

    // Initialize the spaces to allow traps anywhere.
    private static void initSpaces() {
        for(int i = 0; i < Game.ROWS; i++) {
            for(int j = 0; j < Game.COLS; j++) {
                spaces[i][j] = SpaceStatus.TRAP_ALLOWED;
            }
        }
    }

    // Invalidate the locations along some path from start to finish.  This will ensure that the level is solvable.
    // Use levelId to determine the complexity of the path.
    private static void createDefinitePath() {
        do {
            initSpaces();
            curRow = rand.nextInt(Game.ROWS);   // Current row and column that the imaginary player is at.
            curCol = 0;
            curVertDir = rand.nextBoolean();  // Current direction (true = up, false = down)
            curHorizDir = HorizDir.RIGHT;
            spaces[curRow][curCol] = SpaceStatus.TRAP_DISALLOWED;

            while (curCol < Game.COLS - 1) {
                stepVertically();
                if (curRow > 0 && curRow < Game.ROWS - 1)
                    stepHorizontally();
            }
            stepVertically();
        } while(getInvalidSpacesCount() > MAX_PATH_STEPS);
    }

    private static void stepVertically() {
        curRow += (curVertDir)? -1 : 1;
        if(curRow == -1) {
            curRow = 1;
            curVertDir = false;
        }
        else if(curRow == Game.ROWS) {
            curRow = Game.ROWS - 2;
            curVertDir = true;
        }

        spaces[curRow][curCol] = SpaceStatus.TRAP_DISALLOWED;
    }

    private static void stepHorizontally() {
        int dirNumber = rand.nextInt(100);  // Number that will help determine probability that direction will change in next step.

        // Choose the direction
        switch(curHorizDir) {
            case RIGHT:
                rightSteps++;
                if((rightSteps < 5 && dirNumber < 27)
                        || dirNumber < 50
                        || curCol == Game.COLS - 1) {
                    curHorizDir = HorizDir.STRAIGHT;
                    rightSteps = 0;
                }
                break;
            case STRAIGHT:
                straightSteps++;
                if ((straightSteps < 3 && dirNumber < 18)
                        || dirNumber < 37
                        || straightSteps >= 5
                        || curCol == 0) {
                    curHorizDir = HorizDir.RIGHT;
                    straightSteps = 0;
                }
                break;
            default: break;
        }

        // Move in the chosen direction
        if(curHorizDir == HorizDir.RIGHT)
            curCol++;
        spaces[curRow][curCol] = SpaceStatus.TRAP_DISALLOWED;
    }

    // Get the number of traps for this level.
    private static int getNTraps(int levelId) {
        int nTraps;

        if (levelId < 16)
            nTraps = (int)(2.0*Math.log(levelId) + 3);
        else if (levelId < 40)
            nTraps = (int)(0.24*levelId + 5);
        else if (levelId < 100)
            nTraps = (int)(0.1*levelId + 10);
        else if(levelId < 350)
            nTraps = (int)(0.12*levelId + 8);
        else
            nTraps = MAX_TRAPS;

        return nTraps;
    }

    // Get the number of spaces where traps are disallowed.
    private static int getInvalidSpacesCount() {
        int nInvalidSpaces = 0;
        for(SpaceStatus[] row : spaces) {
            for(SpaceStatus status : row) {
                if(status == SpaceStatus.TRAP_DISALLOWED)
                    nInvalidSpaces++;
            }
        }

        return nInvalidSpaces;
    }

    // Create the final traps according to the current list of spaces.
    private static ArrayList<Trap> createFinalTraps() {
        ArrayList<Trap> traps = new ArrayList<>();

        for(int i = 0; i < Game.ROWS; i++) {
            for(int j = 0; j < Game.COLS; j++) {
                if(spaces[i][j] == SpaceStatus.HAS_TRAP) {
                    Trap trap = new Trap(GamePanel.trapImage, Game.TRAP_HEIGHT);
                    trap.placeOnGrid(i, j);
                    traps.add(trap);
                }
            }
        }

        return traps;
    }

    // For testing!!!!!!!!!
    private static void printPath(boolean showTraps) {
        String p = "";
        for(int i = 0; i < Game.ROWS; i++) {
            p += "|";
            for(int j = 0; j < Game.COLS; j++) {
                if(j > 0)
                    p += " ";
                switch(spaces[i][j]) {
                    case TRAP_ALLOWED:  p += " ";   break;
                    case TRAP_DISALLOWED:   p += "X";   break;
                    case HAS_TRAP:  p += (showTraps)? "O" : " ";   break;
                    default:    break;
                }
            }
            p += "|\n";
        }
        System.out.println(p);
    }

}
