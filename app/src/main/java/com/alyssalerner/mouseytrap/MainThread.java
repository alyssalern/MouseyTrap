package com.alyssalerner.mouseytrap;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Alyssa on 2016-03-06.
 */
public class MainThread extends Thread {
    private static final String TAG = "MainThread";
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;    // True when this thread should be running
    private static Canvas canvas;
    private long targetTime = (int)(1000.0 / Game.FPS);   // Time per loop

    public MainThread(SurfaceHolder sh, GamePanel gp) {
        super();
        this.surfaceHolder = sh;
        this.gamePanel = gp;
    }

    @Override
    public void run() {
        long drawTime = 0;
        long updateTime = 0;
        long afterDrawStartTime = 0;
        long startTime = 0;
        while(running) {
            canvas = null;

            // Try locking canvas for pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas();

                // Each game loop, update and draw the game once.
                synchronized (surfaceHolder) {
                    startTime = System.nanoTime();
                    this.gamePanel.draw(canvas);
                    afterDrawStartTime = System.nanoTime();
                    drawTime = (afterDrawStartTime - startTime) / 1000000;
                    this.gamePanel.update();
                    updateTime = (System.nanoTime() - afterDrawStartTime) / 1000000;

                    double drawPortion = (1.0*drawTime / (drawTime + updateTime));
                    double updatePortion = 1.0 - drawPortion;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in game thread: " + e);
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Decide how long to wait
            long timeItTookToDraw = (System.nanoTime() - startTime) / 1000000;

            // DELETE WHEN DONE!
            /*
            if(timeItTookToDraw > targetTime) {
                Log.e(TAG, "Target Time: " + targetTime +
                        "\nActual Time: " + timeItTookToDraw +
                        "\nDraw Time:" + drawTime +
                        "\nUpdate Time: " + updateTime +
                        "\nOther: " + (timeItTookToDraw - drawTime - updateTime) + "\n\n");

                try{ this.sleep(1000);}catch(Exception e){}
            }
            */

            long waitTime = targetTime - timeItTookToDraw;
            // Wait
            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean getRunning() {
        return running;
    }
}