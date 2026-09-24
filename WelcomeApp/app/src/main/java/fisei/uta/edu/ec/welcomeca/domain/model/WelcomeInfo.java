package fisei.uta.edu.ec.welcomeca.domain.model;

public class WelcomeInfo {
    private final String message;
    private final int imageResId;

    public WelcomeInfo(String message, int imageResId) {
        this.message = message;
        this.imageResId = imageResId;
    }

    public String getMessage() {
        return message;
    }

    public int getImageResId() {
        return imageResId;
    }
}