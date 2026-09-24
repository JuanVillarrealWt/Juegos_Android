package fisei.uta.edu.ec.cannongameapp.domain.entities;

public class Target extends GameElement {
    private final int hitReward;

    public Target(int hitReward, int x, int y, int width, int length, float velocityY) {
        super(x, y, width, length, velocityY);
        this.hitReward = hitReward;
    }

    public int getHitReward() {
        return hitReward;
    }
}
