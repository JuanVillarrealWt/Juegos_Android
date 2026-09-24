package fisei.uta.edu.ec.cannongameapp.presentation;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLoopThread extends Thread {
    private static final String TAG = "GameLoopThread";

    public interface GameLoopListener {
        void onGameUpdate(double elapsedTimeMS);
        void onGameRender(Canvas canvas);
    }

    private final SurfaceHolder surfaceHolder;
    private final GameLoopListener listener;
    private volatile boolean isRunning = true;

    public GameLoopThread(SurfaceHolder surfaceHolder, GameLoopListener listener) {
        this.surfaceHolder = surfaceHolder;
        this.listener = listener;
        setName("GameLoopThread");
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        long previousFrameTime = 0;

        while (isRunning) {
            try {
                canvas = surfaceHolder.lockCanvas(null);

                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = 0;

                        if (previousFrameTime != 0) {
                            elapsedTimeMS = currentTime - previousFrameTime;
                            if (elapsedTimeMS > 50.0) {
                                elapsedTimeMS = 50.0;
                            }
                        }
                        previousFrameTime = currentTime;

                        if (listener != null) {
                            listener.onGameUpdate(elapsedTimeMS);
                            listener.onGameRender(canvas);
                        }
                    } else {
                        previousFrameTime = 0;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in game loop", e);
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception unlocking canvas", e);
                    }
                }
            }
        }
    }
}
