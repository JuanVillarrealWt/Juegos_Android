package fisei.uta.edu.ec.cannongameapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;
import fisei.uta.edu.ec.cannongameapp.application.usecases.CheckCollisionsUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.FireCannonballUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.StartNewGameUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.UpdateGamePositionsUseCase;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Blocker;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannonball;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameConfig;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Target;

public class GameLogicTest {

    private static class MockSoundRepository implements SoundRepository {
        boolean targetHitPlayed = false;
        boolean cannonFirePlayed = false;
        boolean blockerHitPlayed = false;

        @Override
        public void playTargetHit() {
            targetHitPlayed = true;
        }

        @Override
        public void playCannonFire() {
            cannonFirePlayed = true;
        }

        @Override
        public void playBlockerHit() {
            blockerHitPlayed = true;
        }

        @Override
        public void release() {}
    }

    private MockSoundRepository mockSoundRepository;

    @Before
    public void setUp() {
        mockSoundRepository = new MockSoundRepository();
    }

    @Test
    public void testStartNewGameCreatesEntitiesCorrectly() {
        StartNewGameUseCase useCase = new StartNewGameUseCase();
        StartNewGameUseCase.GameEntities entities = useCase.execute(800, 480);

        assertNotNull(entities.cannon);
        assertNotNull(entities.blocker);
        assertNotNull(entities.gameState);
        assertEquals(GameConfig.TARGET_PIECES, entities.targets.size());
        assertEquals(10.0, entities.gameState.getTimeLeft(), 0.001);
        assertFalse(entities.gameState.isGameOver());
    }

    @Test
    public void testFireCannonballUseCaseFiresAndPlaysSound() {
        FireCannonballUseCase useCase = new FireCannonballUseCase(mockSoundRepository);
        Cannon cannon = new Cannon(30, 80, 30, 480);
        GameState gameState = new GameState();

        useCase.execute(400, 200, cannon, gameState, 800, 480);

        assertNotNull(cannon.getCannonball());
        assertEquals(1, gameState.getShotsFired());
        assertTrue(mockSoundRepository.cannonFirePlayed);
    }

    @Test
    public void testUpdateGamePositionsUseCaseDecrementsTime() {
        UpdateGamePositionsUseCase useCase = new UpdateGamePositionsUseCase();
        GameState gameState = new GameState();

        useCase.execute(1000, null, null, null, gameState, 800, 480);

        assertEquals(9.0, gameState.getTimeLeft(), 0.001);
        assertEquals(1.0, gameState.getTotalElapsedTime(), 0.001);
        assertFalse(gameState.isGameOver());
    }

    @Test
    public void testCollisionWithTargetAddsRewardAndRemovesTarget() {
        CheckCollisionsUseCase useCase = new CheckCollisionsUseCase(mockSoundRepository);
        Cannon cannon = new Cannon(30, 80, 30, 480);
        Cannonball ball = new Cannonball(100, 100, 15, 100, 0);
        cannon.setCannonball(ball);

        List<Target> targets = new ArrayList<>();
        Target target = new Target(GameConfig.HIT_REWARD, 100, 100, 20, 50, 0);
        targets.add(target);

        GameState gameState = new GameState();
        double initialTime = gameState.getTimeLeft();

        useCase.execute(cannon, null, targets, gameState);

        assertTrue(mockSoundRepository.targetHitPlayed);
        assertEquals(initialTime + GameConfig.HIT_REWARD, gameState.getTimeLeft(), 0.001);
        assertEquals(0, targets.size());
        assertTrue(gameState.isGameWon());
    }

    @Test
    public void testCollisionWithBlockerAppliesPenaltyAndReversesVelocity() {
        CheckCollisionsUseCase useCase = new CheckCollisionsUseCase(mockSoundRepository);
        Cannon cannon = new Cannon(30, 80, 30, 480);
        Cannonball ball = new Cannonball(100, 100, 15, 100, 0);
        cannon.setCannonball(ball);

        Blocker blocker = new Blocker(GameConfig.MISS_PENALTY, 100, 100, 20, 80, 0);
        List<Target> targets = new ArrayList<>();
        targets.add(new Target(3, 500, 500, 20, 50, 0));

        GameState gameState = new GameState();
        double initialTime = gameState.getTimeLeft();

        useCase.execute(cannon, blocker, targets, gameState);

        assertTrue(mockSoundRepository.blockerHitPlayed);
        assertEquals(initialTime - GameConfig.MISS_PENALTY, gameState.getTimeLeft(), 0.001);
        assertTrue(cannon.getCannonball().getVelocityX() < 0);
    }
}
