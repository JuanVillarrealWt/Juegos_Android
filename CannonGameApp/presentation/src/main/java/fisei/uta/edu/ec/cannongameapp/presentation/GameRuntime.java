package fisei.uta.edu.ec.cannongameapp.presentation;

import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;
import fisei.uta.edu.ec.cannongameapp.application.usecases.CheckCollisionsUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.FireCannonballUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.GetGameStateUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.StartNewGameUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.UpdateGamePositionsUseCase;

/** Runtime dependencies supplied by the application's composition root. */
public final class GameRuntime {
    private final SoundRepository soundRepository;
    private final StartNewGameUseCase startNewGameUseCase;
    private final FireCannonballUseCase fireCannonballUseCase;
    private final UpdateGamePositionsUseCase updateGamePositionsUseCase;
    private final CheckCollisionsUseCase checkCollisionsUseCase;
    private final GetGameStateUseCase getGameStateUseCase;

    public GameRuntime(SoundRepository soundRepository, StartNewGameUseCase startNewGameUseCase,
                       FireCannonballUseCase fireCannonballUseCase,
                       UpdateGamePositionsUseCase updateGamePositionsUseCase,
                       CheckCollisionsUseCase checkCollisionsUseCase,
                       GetGameStateUseCase getGameStateUseCase) {
        this.soundRepository = soundRepository;
        this.startNewGameUseCase = startNewGameUseCase;
        this.fireCannonballUseCase = fireCannonballUseCase;
        this.updateGamePositionsUseCase = updateGamePositionsUseCase;
        this.checkCollisionsUseCase = checkCollisionsUseCase;
        this.getGameStateUseCase = getGameStateUseCase;
    }

    public SoundRepository getSoundRepository() { return soundRepository; }
    public StartNewGameUseCase getStartNewGameUseCase() { return startNewGameUseCase; }
    public FireCannonballUseCase getFireCannonballUseCase() { return fireCannonballUseCase; }
    public UpdateGamePositionsUseCase getUpdateGamePositionsUseCase() { return updateGamePositionsUseCase; }
    public CheckCollisionsUseCase getCheckCollisionsUseCase() { return checkCollisionsUseCase; }
    public GetGameStateUseCase getGetGameStateUseCase() { return getGameStateUseCase; }
}
