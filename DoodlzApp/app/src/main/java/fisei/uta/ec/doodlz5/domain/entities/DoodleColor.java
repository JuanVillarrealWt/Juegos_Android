package fisei.uta.ec.doodlz5.domain.entities;

public class DoodleColor {
    private int argbColor;

    public DoodleColor(int argbColor) {
        this.argbColor = argbColor;
    }

    public DoodleColor(int alpha, int red, int green, int blue) {
        this.argbColor = ((alpha & 0xff) << 24) |
                         ((red & 0xff) << 16) |
                         ((green & 0xff) << 8) |
                         (blue & 0xff);
    }

    public int getArgbColor() {
        return argbColor;
    }

    public void setArgbColor(int argbColor) {
        this.argbColor = argbColor;
    }

    public int getAlpha() {
        return (argbColor >> 24) & 0xff;
    }

    public int getRed() {
        return (argbColor >> 16) & 0xff;
    }

    public int getGreen() {
        return (argbColor >> 8) & 0xff;
    }

    public int getBlue() {
        return argbColor & 0xff;
    }
}
