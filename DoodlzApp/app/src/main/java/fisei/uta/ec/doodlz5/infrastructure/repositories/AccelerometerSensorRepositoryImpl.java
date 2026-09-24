package fisei.uta.ec.doodlz5.infrastructure.repositories;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import fisei.uta.ec.doodlz5.application.contracts.repositories.ShakeSensorRepository;
import fisei.uta.ec.doodlz5.application.usecases.EvaluateShakeUseCase;

public class AccelerometerSensorRepositoryImpl implements ShakeSensorRepository {
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private final EvaluateShakeUseCase evaluateShakeUseCase;
    private float currentAcceleration = SensorManager.GRAVITY_EARTH;
    private float lastAcceleration = SensorManager.GRAVITY_EARTH;

    public AccelerometerSensorRepositoryImpl(EvaluateShakeUseCase evaluateShakeUseCase) {
        this.evaluateShakeUseCase = evaluateShakeUseCase;
    }

    @Override
    public void startListening(Context context, OnShakeListener listener) {
        if (context == null) return;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) return;

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) return;

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                lastAcceleration = currentAcceleration;
                currentAcceleration = x * x + y * y + z * z;

                if (evaluateShakeUseCase != null && evaluateShakeUseCase.isShakeDetected(currentAcceleration, lastAcceleration)) {
                    if (listener != null) {
                        listener.onShakeDetected();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stopListening(Context context) {
        if (sensorManager != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
            sensorEventListener = null;
            sensorManager = null;
        }
    }
}
