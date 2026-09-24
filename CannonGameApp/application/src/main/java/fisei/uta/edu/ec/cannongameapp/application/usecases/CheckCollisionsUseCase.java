package fisei.uta.edu.ec.cannongameapp.application.usecases;

import java.util.List;

import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Blocker;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Target;

public class CheckCollisionsUseCase {
    private final SoundRepository soundRepository;

    public CheckCollisionsUseCase(SoundRepository soundRepository) {
        this.soundRepository = soundRepository;
    }

    public void execute(Cannon cannon, Blocker blocker, List<Target> targets, GameState gameState) {
        if (cannon == null || gameState == null || gameState.isGameOver()) {
            return;
        }

        if (cannon.getCannonball() != null && cannon.getCannonball().isOnScreen()) {
            if (targets != null) {
                for (int n = 0; n < targets.size(); n++) {
                    Target target = targets.get(n);
                    if (cannon.getCannonball() != null && cannon.getCannonball().collidesWith(target)) {
                        if (soundRepository != null) {
                            soundRepository.playTargetHit();
                        }
                        gameState.applyHitReward(target.getHitReward());
                        cannon.removeCannonball();
                        targets.remove(n);
                        break;
                    }
                }
            }
        } else if (cannon.getCannonball() != null) {
            cannon.removeCannonball();
        }

        if (cannon.getCannonball() != null && cannon.getCannonball().isOnScreen()) {
            if (blocker != null && cannon.getCannonball().collidesWith(blocker)) {
                if (soundRepository != null) {
                    soundRepository.playBlockerHit();
                }
                cannon.getCannonball().reverseVelocityX();
                gameState.applyMissPenalty(blocker.getMissPenalty());
            }
        }

        if (targets != null && targets.isEmpty()) {
            gameState.setGameWon();
        }
    }
}
