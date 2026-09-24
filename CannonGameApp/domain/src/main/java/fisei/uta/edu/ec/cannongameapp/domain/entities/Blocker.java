package fisei.uta.edu.ec.cannongameapp.domain.entities;

public class Blocker extends GameElement {
    private final int missPenalty;

    public Blocker(int missPenalty, int x, int y, int width, int length, float velocityY) {
        super(x, y, width, length, velocityY);
        this.missPenalty = missPenalty;
    }

    public int getMissPenalty() {
        return missPenalty;
    }
}
