package fisei.uta.edu.ec.cannongameapp.application.usecases;

import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;

public class GetGameStateUseCase {

    public boolean isGameOver(GameState gameState) {
        return gameState != null && gameState.isGameOver();
    }

    public boolean isGameWon(GameState gameState) {
        return gameState != null && gameState.isGameWon();
    }

    public double getTimeLeft(GameState gameState) {
        return gameState != null ? gameState.getTimeLeft() : 0.0;
    }

    public int getShotsFired(GameState gameState) {
        return gameState != null ? gameState.getShotsFired() : 0;
    }

    public double getTotalElapsedTime(GameState gameState) {
        return gameState != null ? gameState.getTotalElapsedTime() : 0.0;
    }
}
