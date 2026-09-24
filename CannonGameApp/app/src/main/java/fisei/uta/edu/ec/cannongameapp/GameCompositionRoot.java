package fisei.uta.edu.ec.cannongameapp;

import android.content.Context;

import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;
import fisei.uta.edu.ec.cannongameapp.application.usecases.CheckCollisionsUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.FireCannonballUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.GetGameStateUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.StartNewGameUseCase;
import fisei.uta.edu.ec.cannongameapp.application.usecases.UpdateGamePositionsUseCase;
import fisei.uta.edu.ec.cannongameapp.infrastructure.repositories.SoundPoolRepository;
import fisei.uta.edu.ec.cannongameapp.presentation.GameRuntime;

/** Creates the application's concrete adapters and wires them to use cases. */
public final class GameCompositionRoot {
    private GameCompositionRoot() { }

    public static GameRuntime create(Context context) {
        SoundRepository soundRepository = new SoundPoolRepository(context);
        return new GameRuntime(soundRepository,
                new StartNewGameUseCase(),
                new FireCannonballUseCase(soundRepository),
                new UpdateGamePositionsUseCase(),
                new CheckCollisionsUseCase(soundRepository),
                new GetGameStateUseCase());
    }
}
