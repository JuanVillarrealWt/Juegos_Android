package fisei.uta.edu.ec.cannongameapp.domain.entities;

public class Cannon {
    private final int baseRadius;
    private final int barrelLength;
    private final int barrelWidth;
    private double barrelAngle;
    private Cannonball cannonball;

    public Cannon(int baseRadius, int barrelLength, int barrelWidth, int screenHeight) {
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        this.barrelWidth = barrelWidth;
        align(Math.PI / 2, screenHeight);
    }

    public void align(double barrelAngle, int screenHeight) {
        this.barrelAngle = barrelAngle;
    }

    public Cannonball fireCannonball(int screenWidth, int screenHeight) {
        int velocityX = (int) (GameConfig.CANNONBALL_SPEED_PERCENT * screenWidth * Math.sin(barrelAngle));
        int velocityY = (int) (GameConfig.CANNONBALL_SPEED_PERCENT * screenWidth * -Math.cos(barrelAngle));
        int radius = (int) (screenHeight * GameConfig.CANNONBALL_RADIUS_PERCENT);
        cannonball = new Cannonball(-radius, screenHeight / 2 - radius, radius, velocityX, velocityY);
        return cannonball;
    }

    public Cannonball getCannonball() { return cannonball; }
    public void setCannonball(Cannonball cannonball) { this.cannonball = cannonball; }
    public void removeCannonball() { this.cannonball = null; }
    public double getBarrelAngle() { return barrelAngle; }
    public int getBarrelEndX() { return (int) (barrelLength * Math.sin(barrelAngle)); }
    public int getBarrelEndY(int screenHeight) {
        return (int) (-barrelLength * Math.cos(barrelAngle)) + screenHeight / 2;
    }
    public int getBaseRadius() { return baseRadius; }
    public int getBarrelLength() { return barrelLength; }
    public int getBarrelWidth() { return barrelWidth; }
}
