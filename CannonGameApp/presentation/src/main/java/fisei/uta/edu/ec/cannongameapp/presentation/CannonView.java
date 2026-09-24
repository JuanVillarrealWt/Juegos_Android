package fisei.uta.edu.ec.cannongameapp.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import java.util.List;

import fisei.uta.edu.ec.cannongameapp.presentation.R;
import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;
import fisei.uta.edu.ec.cannongameapp.application.usecases.CheckCollisionsUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.FireCannonballUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.GetGameStateUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.StartNewGameUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.UpdateGamePositionsUseCase;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Blocker;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Target;
import fisei.uta.edu.ec.cannongameapp.presentation.GameRuntime;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback, GameLoopThread.GameLoopListener {
    private static final String TAG = "CannonView";

    private boolean dialogIsDisplayed = false;

    // Use cases
    private StartNewGameUseCase startNewGameUseCase;
    private FireCannonballUseCase fireCannonballUseCase;
    private UpdateGamePositionsUseCase updateGamePositionsUseCase;
    private CheckCollisionsUseCase checkCollisionsUseCase;
    private GetGameStateUseCase getGameStateUseCase;

    // Infrastructure & Presentation helpers
    private SoundRepository soundRepository;
    private GameRuntime gameRuntime;
    private final CannonRenderer cannonRenderer;
    private GameLoopThread gameLoopThread;

    // Game Entities
    private Cannon cannon;
    private Blocker blocker;
    private List<Target> targets;
    private GameState gameState;

    // Dimensions
    private int screenWidth = 0;
    private int screenHeight = 0;

    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);

        this.cannonRenderer = new CannonRenderer(context);
    }

    public void setGameRuntime(GameRuntime gameRuntime) {
        this.gameRuntime = gameRuntime;
        this.soundRepository = gameRuntime.getSoundRepository();
        this.startNewGameUseCase = gameRuntime.getStartNewGameUseCase();
        this.fireCannonballUseCase = gameRuntime.getFireCannonballUseCase();
        this.updateGamePositionsUseCase = gameRuntime.getUpdateGamePositionsUseCase();
        this.checkCollisionsUseCase = gameRuntime.getCheckCollisionsUseCase();
        this.getGameStateUseCase = gameRuntime.getGetGameStateUseCase();
        newGame();
        if (getHolder().getSurface().isValid()) {
            startGameLoop();
        }
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.screenWidth = w;
        this.screenHeight = h;

        cannonRenderer.updateDimensions(screenHeight);
        newGame();
    }

    public void newGame() {
        if (startNewGameUseCase == null) {
            return;
        }
        if (screenWidth <= 0 || screenHeight <= 0) {
            if (getWidth() > 0 && getHeight() > 0) {
                screenWidth = getWidth();
                screenHeight = getHeight();
                cannonRenderer.updateDimensions(screenHeight);
            } else {
                return;
            }
        }

        StartNewGameUseCase.GameEntities entities = startNewGameUseCase.execute(
                screenWidth, screenHeight);

        this.cannon = entities.cannon;
        this.blocker = entities.blocker;
        this.targets = entities.targets;
        this.gameState = entities.gameState;

        hideSystemBars();
    }

    @Override
    public void onGameUpdate(double elapsedTimeMS) {
        if (updateGamePositionsUseCase == null || gameState == null || gameState.isGameOver() ||
                screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        updateGamePositionsUseCase.execute(elapsedTimeMS, cannon, blocker, targets,
                gameState, screenWidth, screenHeight);

        checkCollisionsUseCase.execute(cannon, blocker, targets, gameState);

        if (getGameStateUseCase.isGameOver(gameState)) {
            stopGame();
            int messageId = getGameStateUseCase.isGameWon(gameState) ? R.string.win : R.string.lose;
            showGameOverDialog(messageId);
        }
    }

    @Override
    public void onGameRender(Canvas canvas) {
        if (canvas != null && screenWidth > 0 && screenHeight > 0) {
            cannonRenderer.render(canvas, cannon, blocker, targets, gameState, screenWidth, screenHeight);
        }
    }

    private void showGameOverDialog(final int messageId) {
        if (dialogIsDisplayed) {
            return;
        }
        dialogIsDisplayed = true;
        showSystemBars();

        Activity act = getActivity();
        if (act != null) {
            GameOverDialogHelper.showGameOverDialog(
                    act,
                    messageId,
                    getGameStateUseCase.getShotsFired(gameState),
                    getGameStateUseCase.getTotalElapsedTime(gameState),
                    new GameOverDialogHelper.OnResetGameListener() {
                        @Override
                        public void onResetGame() {
                            dialogIsDisplayed = false;
                            newGame();
                            if (gameLoopThread == null || !gameLoopThread.isRunning()) {
                                gameLoopThread = new GameLoopThread(getHolder(), CannonView.this);
                                gameLoopThread.setRunning(true);
                                gameLoopThread.start();
                            }
                        }
                    }
            );
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            if (fireCannonballUseCase != null && screenWidth > 0 && screenHeight > 0 &&
                    gameState != null && !gameState.isGameOver()) {
                fireCannonballUseCase.execute(e.getX(), e.getY(), cannon, gameState, screenWidth, screenHeight);
            }
        }

        return true;
    }

    public void stopGame() {
        if (gameLoopThread != null) {
            gameLoopThread.setRunning(false);
        }
    }

    private void startGameLoop() {
        if (gameLoopThread == null || !gameLoopThread.isRunning()) {
            gameLoopThread = new GameLoopThread(getHolder(), this);
            gameLoopThread.setRunning(true);
            gameLoopThread.start();
        }
    }

    public void releaseResources() {
        stopGame();
        if (soundRepository != null) {
            soundRepository.release();
            soundRepository = null;
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (!dialogIsDisplayed && startNewGameUseCase != null) {
            newGame();
            startGameLoop();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (width > 0 && height > 0) {
            this.screenWidth = width;
            this.screenHeight = height;
            cannonRenderer.updateDimensions(height);
            if (gameState == null) {
                newGame();
            }
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        stopGame();

        while (retry && gameLoopThread != null) {
            try {
                gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
        gameLoopThread = null;
    }

    private void hideSystemBars() {
        post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
            }
        });
    }

    private void showSystemBars() {
        post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        });
    }
}
