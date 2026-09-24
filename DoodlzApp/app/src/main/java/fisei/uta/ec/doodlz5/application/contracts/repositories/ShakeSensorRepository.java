package fisei.uta.ec.doodlz5.application.contracts.repositories;

import android.content.Context;

public interface ShakeSensorRepository {
    interface OnShakeListener {
        void onShakeDetected();
    }

    void startListening(Context context, OnShakeListener listener);
    void stopListening(Context context);
}
