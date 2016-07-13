package com.alyssalerner.mouseytrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/** Note: 'width' and 'height' refer throughout this program to the dimensions relative to
 * BASE_WIDTH and BASE_HEIGHT, which will always be scaled to device's actual dimensions.
 * Created by Alyssa on 2016-03-06.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    public static String TAG = "GamePanel";

    public static int screenWidth;  // Device's actual width in pixels
    public static int screenHeight; // Device's actual height in pixels

    private Context context;
    private MainThread thread;  // Thread that runs the game loop
    public static Bitmap trapImage;
    public static Bitmap timerImage;
    public static Bitmap cheeseImage;
    private static LevelLoader levelLoader;

    private Overlay overlay;    // Contains information about lives, etc. which are displayed
    private Player player;  // The player controlled by the user
    private Background background;  // The game's background.
    private Level level;    // Current level
    private Level levelBeingLoaded;
    private static boolean transitioning = false;   // True when currently transitioning to a new game.

    // Normally playerDir0 will be true when player is moving right and false when moving left.
    // If two fingers on screen, playerDir0 will carry value from first finger and playerDir1 will carry second.
    private boolean playerDir0;
    private boolean playerDir1;

    public GamePanel(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!gameStarted()) {
            gameInit();
            startNewGame();
        }

        // Start the game loop
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    // Stuff to do only once when the app starts up
    private void gameInit() {
        this.screenWidth = getWidth();
        this.screenHeight = getHeight();

        trapImage = BitmapFactory.decodeResource(getResources(), Game.getResId(Game.trapImage, R.drawable.class));
        cheeseImage = BitmapFactory.decodeResource(getResources(), Game.getResId(Game.cheeseImage, R.drawable.class));
        timerImage = BitmapFactory.decodeResource(getResources(), Game.getResId(Game.timerImage, R.drawable.class));
        background = new Background(BitmapFactory.decodeResource(getResources(), Game.getResId(Game.backgroundImage, R.drawable.class)));
        levelLoader = new LevelLoader();
        overlay = new Overlay(this, context);

        player = new Player(
                BitmapFactory.decodeResource(getResources(), Game.getResId(Game.playerImage, R.drawable.class)),
                Game.PLAYER_HEIGHT
        );

        // Retrieve all the cat paw images
        Bitmap[] catPaws = new Bitmap[Game.N_PAWS];
        String pawFileName;
        for(int i = 0; i < Game.N_PAWS; i++) {
            pawFileName = "cat_paw_" + (i+1);
            catPaws[i] = BitmapFactory.decodeResource(getResources(), Game.getResId(pawFileName, R.drawable.class));
        }
        LostGameTransitioner.initialize(catPaws);
    }

    // Start the game at level 1
    private void startNewGame() {
        levelLoader.reset();
        levelBeingLoaded = null;
        level = levelLoader.getCurLevel();
        background.setOffset(0);
        levelLoader.resetOffsets();
        player.setCanMove(true);
        player.setOffset(0);
        player.positionAtStart(true);
        overlay.reset();
        player.setVisibility(true);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;    // Ensure don't enter an infinite loop
        // Try to destroy the thread until successful (can take a few attempts)
        while(retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;  // So garbage collector can pick up object
            } catch(InterruptedException e) { e.printStackTrace();}
        }
        ScoreKeeper.saveHighScore();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Determine if game has started
    private boolean gameStarted() {
        return (levelLoader != null);
    }

    // Return true if player is allowed to move.
    private boolean playerCanMove() {
        return levelLoader.getLoadingState() == LevelLoader.LoadingState.NONE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action;
        boolean right;
        if (playerCanMove()) {
            action = event.getActionMasked();
            right = (event.getX(event.getActionIndex()) > screenWidth / 2);


            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    player.moveInDirection(right, player.getMovingDown());
                    playerDir0 = right;
                    break;

                case MotionEvent.ACTION_UP:
                    player.stopHorizontalMovement();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    player.moveInDirection(right, player.getMovingDown());
                    playerDir1 = right;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    playerDir0 = playerDir1;
                    player.moveInDirection(playerDir0, player.getMovingDown());
                    break;
                default:
                    break;
            }
        }

        return true;
    }
    // Check if player has collided with a trap, cheese or the exit and perform appropriate responses.
    private void checkForCollisions() {
        // Check if player hits a trap
        for(Trap trap : level.getTraps()) {
            if(player.collidesWith(trap)) {
                player.positionAtStart(true);
                player.stopHorizontalMovement();
            }
        }

        // Check if player hits the cheese
        ArrayList<Cheese> cheeses = level.getCheese();
        for(Cheese cheese : cheeses) {
            if(player.collidesWith(cheese)) {
                overlay.addToTimer(Game.CHEESE_TIME_VALUE);
                overlay.addToScore(1);
                level.removeCheese(cheese);
            }
        }

        // Once player reaches exit, load next level.
        if (player.getX() > Game.SLIDE_THRESHOLD) {
            levelLoader.loadNextLevel();
            levelBeingLoaded = levelLoader.getNextLevel();
            player.stopHorizontalMovement();
        }
    }

    public void draw(Canvas canvas) {
            long time0 = System.nanoTime();
        background.draw(canvas);
            long time1 = System.nanoTime();
        level.draw(canvas);
            long time2 = System.nanoTime();
        if(levelBeingLoaded != null)
            levelBeingLoaded.draw(canvas);
            long time3 = System.nanoTime();
        player.draw(canvas);
            long time4 = System.nanoTime();
        if(transitioning)
            LostGameTransitioner.draw(canvas);
            long time5 = System.nanoTime();
        overlay.draw(canvas);
            long time6 = System.nanoTime();

        long drawBg = time1 - time0;
        long drawLevel = time2 - time1;
        long drawLoading = time3 - time2;
        long drawPlayer = time4 - time3;
        long drawTrans = time5 - time4;
        long drawOverlay = time6 - time5;
        long drawTotal = time6 - time0;

        /*
        Log.i(TAG, "DRAW TIMES:");
        Log.i(TAG, "\tBackground: " + drawBg + "(" + (int)(100.0*drawBg/drawTotal) + "%");
        Log.i(TAG, "\tLevel: " + drawLevel + "(" + (int)(100.0*drawLevel/drawTotal) + "%");
        Log.i(TAG, "\tLevel loader: " + drawLoading + "(" + (int)(100.0*drawLoading/drawTotal) + "%");
        Log.i(TAG, "\tPlayer: " + drawPlayer + "(" + (int)(100.0*drawPlayer/drawTotal) + "%");
        Log.i(TAG, "\tTransitioner: " + drawTrans + "(" + (int)(100.0*drawTrans/drawTotal) + "%");
        Log.i(TAG, "\tOverlay: " + drawOverlay + "(" + (int)(100.0*drawOverlay/drawTotal) + "%");
        System.out.println();
        */

    }


    public void update() {
        if(transitioning) {
            if (LostGameTransitioner.stillTransitioning()) {
                LostGameTransitioner.update();
            } else {
                transitioning = false;
                startNewGame();
            }
        }
        // See if timer has run out
        else if(overlay.outOfTime()) {
            LostGameTransitioner.startTransition(player);
            overlay.updateHighScore();
            transitioning = true;
        }

        // Normal update activity, when not transitioning to new level
        else if(levelLoader.getLoadingState() == LevelLoader.LoadingState.NONE) {
            checkForCollisions();
        }
        // When finished loading a level
        else if(!levelLoader.levelStillLoading()) {
            level = levelLoader.getCurLevel();
            levelBeingLoaded = null;
        }
        // When loading a level
        else {
            levelBeingLoaded.update();
        }
        if(levelLoader.getLoadingState() == LevelLoader.LoadingState.LOADING)
            levelLoader.update(player, background);
        level.update();
        background.update();
        player.update();
        overlay.update();
    }

}
