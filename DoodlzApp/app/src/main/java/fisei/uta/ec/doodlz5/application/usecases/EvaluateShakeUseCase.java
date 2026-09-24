package fisei.uta.ec.doodlz5.application.usecases;

public class EvaluateShakeUseCase {
    private static final float ACCELERATION_THRESHOLD = 100000f;

    public boolean isShakeDetected(float currentAcceleration, float lastAcceleration) {
        float acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);
        return acceleration > ACCELERATION_THRESHOLD;
    }
}
