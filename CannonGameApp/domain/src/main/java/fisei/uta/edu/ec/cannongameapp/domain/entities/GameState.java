package fisei.uta.edu.ec.cannongameapp.domain.entities;

public class GameState {
    private double timeLeft;
    private int shotsFired;
    private double totalElapsedTime;
    private boolean gameOver;
    private boolean gameWon;

    public GameState() {
        reset();
    }

    public void reset() {
        this.timeLeft = GameConfig.INITIAL_TIME_SECONDS;
        this.shotsFired = 0;
        this.totalElapsedTime = 0.0;
        this.gameOver = false;
        this.gameWon = false;
    }

    public void incrementElapsedTime(double seconds) {
        this.totalElapsedTime += seconds;
        this.timeLeft -= seconds;
        if (this.timeLeft <= 0) {
            this.timeLeft = 0.0;
            this.gameOver = true;
            this.gameWon = false;
        }
    }

    public void incrementShotsFired() {
        this.shotsFired++;
    }

    public void applyHitReward(int seconds) {
        this.timeLeft += seconds;
    }

    public void applyMissPenalty(int seconds) {
        this.timeLeft -= seconds;
        if (this.timeLeft <= 0) {
            this.timeLeft = 0.0;
            this.gameOver = true;
            this.gameWon = false;
        }
    }

    public void setGameWon() {
        this.gameOver = true;
        this.gameWon = true;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public double getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(double timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public double getTotalElapsedTime() {
        return totalElapsedTime;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }
}
