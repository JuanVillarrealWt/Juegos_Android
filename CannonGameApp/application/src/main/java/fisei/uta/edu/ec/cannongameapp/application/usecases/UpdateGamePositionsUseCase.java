package fisei.uta.edu.ec.cannongameapp.application.usecases;

import java.util.List;

import fisei.uta.edu.ec.cannongameapp.domain.entities.Blocker;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Target;

public class UpdateGamePositionsUseCase {

    public void execute(double elapsedTimeMS, Cannon cannon, Blocker blocker,
                        List<Target> targets, GameState gameState,
                        int screenWidth, int screenHeight) {
        if (gameState == null || gameState.isGameOver()) {
            return;
        }

        double interval = elapsedTimeMS / 1000.0;

        if (cannon != null && cannon.getCannonball() != null) {
            cannon.getCannonball().update(interval, screenWidth, screenHeight);
        }

        if (blocker != null) {
            blocker.update(interval, screenHeight);
        }

        if (targets != null) {
            for (Target target : targets) {
                target.update(interval, screenHeight);
            }
        }

        gameState.incrementElapsedTime(interval);
    }
}
