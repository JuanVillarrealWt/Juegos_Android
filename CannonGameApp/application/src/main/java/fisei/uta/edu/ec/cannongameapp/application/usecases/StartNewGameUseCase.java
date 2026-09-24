package fisei.uta.edu.ec.cannongameapp.application.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fisei.uta.edu.ec.cannongameapp.domain.entities.Blocker;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameConfig;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Target;

public class StartNewGameUseCase {
    private final Random random;

    public StartNewGameUseCase() {
        this.random = new Random();
    }

    public StartNewGameUseCase(Random random) {
        this.random = random;
    }

    public static class GameEntities {
        public final Cannon cannon;
        public final Blocker blocker;
        public final List<Target> targets;
        public final GameState gameState;

        public GameEntities(Cannon cannon, Blocker blocker, List<Target> targets, GameState gameState) {
            this.cannon = cannon;
            this.blocker = blocker;
            this.targets = targets;
            this.gameState = gameState;
        }
    }

    public GameEntities execute(int screenWidth, int screenHeight) {
        Cannon cannon = new Cannon(
                (int) (GameConfig.CANNON_BASE_RADIUS_PERCENT * screenHeight),
                (int) (GameConfig.CANNON_BARREL_LENGTH_PERCENT * screenWidth),
                (int) (GameConfig.CANNON_BARREL_WIDTH_PERCENT * screenHeight),
                screenHeight
        );

        List<Target> targets = new ArrayList<>();
        int targetX = (int) (GameConfig.TARGET_FIRST_X_PERCENT * screenWidth);
        int targetY = (int) ((0.5 - GameConfig.TARGET_LENGTH_PERCENT / 2) * screenHeight);

        for (int n = 0; n < GameConfig.TARGET_PIECES; n++) {
            double velocity = screenHeight * (random.nextDouble() *
                    (GameConfig.TARGET_MAX_SPEED_PERCENT - GameConfig.TARGET_MIN_SPEED_PERCENT) +
                    GameConfig.TARGET_MIN_SPEED_PERCENT);

            velocity *= -1;

            targets.add(new Target(
                    GameConfig.HIT_REWARD,
                    targetX,
                    targetY,
                    (int) (GameConfig.TARGET_WIDTH_PERCENT * screenWidth),
                    (int) (GameConfig.TARGET_LENGTH_PERCENT * screenHeight),
                    (float) velocity
            ));

            targetX += (GameConfig.TARGET_WIDTH_PERCENT + GameConfig.TARGET_SPACING_PERCENT) * screenWidth;
        }

        Blocker blocker = new Blocker(
                GameConfig.MISS_PENALTY,
                (int) (GameConfig.BLOCKER_X_PERCENT * screenWidth),
                (int) ((0.5 - GameConfig.BLOCKER_LENGTH_PERCENT / 2) * screenHeight),
                (int) (GameConfig.BLOCKER_WIDTH_PERCENT * screenWidth),
                (int) (GameConfig.BLOCKER_LENGTH_PERCENT * screenHeight),
                (float) (GameConfig.BLOCKER_SPEED_PERCENT * screenHeight)
        );

        GameState gameState = new GameState();

        return new GameEntities(cannon, blocker, targets, gameState);
    }
}
