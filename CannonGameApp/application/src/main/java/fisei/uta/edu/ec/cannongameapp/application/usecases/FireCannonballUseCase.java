package fisei.uta.edu.ec.cannongameapp.application.usecases;

import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;

public class FireCannonballUseCase {
    private final SoundRepository soundRepository;

    public FireCannonballUseCase(SoundRepository soundRepository) {
        this.soundRepository = soundRepository;
    }

    public void execute(float touchX, float touchY, Cannon cannon, GameState gameState,
                        int screenWidth, int screenHeight) {
        if (cannon == null || gameState == null || gameState.isGameOver()) {
            return;
        }

        double centerMinusY = (screenHeight / 2.0 - touchY);
        double angle = Math.atan2(touchX, centerMinusY);

        cannon.align(angle, screenHeight);

        if (cannon.getCannonball() == null || !cannon.getCannonball().isOnScreen()) {
            cannon.fireCannonball(screenWidth, screenHeight);
            gameState.incrementShotsFired();
            if (soundRepository != null) {
                soundRepository.playCannonFire();
            }
        }
    }
}
