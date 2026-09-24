package fisei.uta.edu.ec.cannongameapp.domain.entities;

public class Cannonball extends GameElement {
    private float velocityX;
    private boolean onScreen;
    private float floatX;

    public Cannonball(int x, int y, int radius, float velocityX, float velocityY) {
        super(x, y, 2 * radius, 2 * radius, velocityY);
        this.velocityX = velocityX;
        this.onScreen = true;
        this.floatX = x;
    }

    public int getRadius() { return width / 2; }

    public boolean collidesWith(GameElement element) {
        return element != null && velocityX > 0 &&
                x < element.getX() + element.getWidth() &&
                x + width > element.getX() &&
                y < element.getY() + element.getHeight() &&
                y + height > element.getY();
    }

    public boolean isOnScreen() { return onScreen; }
    public void setOnScreen(boolean onScreen) { this.onScreen = onScreen; }
    public void reverseVelocityX() { velocityX *= -1; }
    public float getVelocityX() { return velocityX; }

    public void update(double interval, int screenWidth, int screenHeight) {
        super.update(interval, screenHeight);
        floatX += (float) (velocityX * interval);
        x = Math.round(floatX);
        if (x + width < 0 || x > screenWidth || y + height < 0 || y > screenHeight) {
            onScreen = false;
        }
    }
}
