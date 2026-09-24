package fisei.uta.edu.ec.cannongameapp.domain.entities;

/** Framework-independent moving rectangle used by the game rules. */
public class GameElement {
    protected int x;
    protected int y;
    protected final int width;
    protected final int height;
    private float velocityY;
    protected float floatY;

    public GameElement(int x, int y, int width, int height, float velocityY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floatY = y;
        this.velocityY = velocityY;
    }

    public void update(double interval, int screenHeight) {
        floatY += (float) (velocityY * interval);
        y = Math.round(floatY);

        if ((y < 0 && velocityY < 0) || (y + height > screenHeight && velocityY > 0)) {
            velocityY *= -1;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityY(float velocityY) { this.velocityY = velocityY; }
}
