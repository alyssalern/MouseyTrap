package com.alyssalerner.mouseytrap;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/** Responsible for loading the next level.
 * Usage:   Call loadNextLevel() to begin loading the next level.  Then, keep calling levelStillLoading()
 *          until it returns false.  Once it returns false, getCurLevel() should be called to retrieve
 *          the already-created level that was just loaded.
 * Created by Alyssa on 2016-03-30.
 */
public class LevelLoader {
    private static final String TAG = "LevelLoader";
    private Random rand = new Random();

    /* The state of loading that the level loader is currently in.
     * LOADING: Currently in process of loading a level.
     * FINISHED: Level finished loading, but this status has not yet been detected by main program.
     * NONE: Set after FINISHED has been detected by main program.
     */
    public enum LoadingState {LOADING, FINISHED, NONE}

    private Level curLevel;
    private Level nextLevel;
    private LoadingState loadingState = LoadingState.NONE;
    private double panSpeedPx; // Pan speed in pixels per update
    private double panOffset;  // Current amount that the view has been panned while transitioning to a new level
    private double offsetGoal; // Total offset to pan before a level is done loading

    /* Explanation of how nCheeses works:
     * Every 3rd level starting with level 1 (ie. level 1, 4, 7, 10, ...) will have 1 cheese.
     * Each pair of levels after these numbers (ie. 2 and 3, 5 and 6, ...) will have 2 cheeses distributed between them.
     * The nCheeses array will contain the number of cheeses in these pairs of levels. Eg. From levels 1-3, nCheeses[0]
     * will contain the number of cheeses in level 2, and nCheeses[1] will contain the number in level 3.
     */
    private int[] nCheeses = new int[2];    // Number of cheeses in the next 2 levels

    public LevelLoader() {
        curLevel = retrieveLevel(Game.START_LEVEL);
        this.offsetGoal = Game.BASE_WIDTH - Game.SAFE_SPACE_WIDTH;
        panSpeedPx = Game.convertToPixelX(1.0*Game.PAN_SPEED);
    }


    /* Begin loading next level, including panning the view.
     */
    public void loadNextLevel() {
        loadingState = LoadingState.LOADING;
        nextLevel = retrieveLevel(curLevel.getId() + 1);
        nextLevel.setOffset(Game.BASE_WIDTH - Game.SAFE_SPACE_WIDTH);
        nextLevel.setLoading(true);
    }

    /* Returns true if the next level is still loading.
     * Req: This method should only be called once after calling loadNextLevel().
     *
     */
    public boolean levelStillLoading() {
        if(loadingState == LoadingState.FINISHED) {
            loadingState = LoadingState.NONE;
            return false;
        }
        return true;
    }

    public Level getCurLevel() {
        return curLevel;
    }

    public Level getNextLevel() {
        return nextLevel;
    }

    public LoadingState getLoadingState() {
        return loadingState;
    }

    // Reset to level 1
    public void reset() {
        curLevel.resetIds();
        curLevel = retrieveLevel(Game.START_LEVEL);
        loadingState = LoadingState.NONE;
        nextLevel = null;
    }

    /* Create the given level.
     * levelNum: The level to create, starting at 1.
     */
    private Level retrieveLevel(int levelNum) {

        ArrayList<Trap> traps = TrapCreator.createTraps(levelNum);

        int nCheeses = getNCheeses(levelNum);
        ArrayList<Cheese> cheeses = new ArrayList<>();

        // Add cheeses to this level at random positions.
        for(int i = 0; i < nCheeses; i++) {
            cheeses.add(getRandomCheese(traps, cheeses));
        }
        return new Level(traps, cheeses);
    }

    /* Get the number of cheeses for this level.
     * levelId: The level's id.
     */
    private int getNCheeses(int levelId) {
        int curNCheeses = 0;
        switch (levelId % 3) {
            case 1:
                nCheeses[0] = 0;
                nCheeses[1] = 0;
                nCheeses[rand.nextBoolean()? 1 : 0]++;
                nCheeses[rand.nextBoolean()? 1 : 0]++;
                curNCheeses = 1;
                break;
            case 2:
                curNCheeses = nCheeses[0];
                break;
            case 0:
                curNCheeses = nCheeses[1];
                break;
            default: break;
        }
        return curNCheeses;
    }

    /* Return a randomly placed cheese.
     * traps: The trap obstacles to avoid
     * cheeses: The cheese obstacles to avoid
     */
    private Cheese getRandomCheese(ArrayList<Trap> traps, ArrayList<Cheese> cheeses) {
        Cheese randCheese = new Cheese(GamePanel.cheeseImage, Game.CHEESE_HEIGHT);
        ArrayList<GameObject> obstacles = new ArrayList<>();
        for(Cheese cheese : cheeses)
            obstacles.add(cheese.copy());
        for(Trap trap : traps)
            obstacles.add(trap.copy());
        randCheese.placeAtRandomSpot(obstacles);
        return randCheese;
    }

    public void resetOffsets() {
        curLevel.setOffset(0);
        panOffset = 0;
    }

    // If loading, Continue panning the view.
    public void update(Player player, Background background) {
        if(loadingState == LoadingState.LOADING) {
            player.offsetBy(-1*Game.PAN_SPEED);
            background.offsetBy(-1*Game.PAN_SPEED);
            curLevel.offsetBy(-1*Game.PAN_SPEED);
            nextLevel.offsetBy(-1*Game.PAN_SPEED);

            panOffset = (panOffset + Game.PAN_SPEED <= offsetGoal)? (panOffset + Game.PAN_SPEED) : offsetGoal;

            // Detect when finished panning view
            if(panOffset == offsetGoal) {
                loadingState = LoadingState.FINISHED;
                curLevel = nextLevel;
                nextLevel = null;
                curLevel.setLoading(false);
                resetOffsets();
                player.setOffset(0);
                player.positionAtStart(false);
            }

        }
    }
}
