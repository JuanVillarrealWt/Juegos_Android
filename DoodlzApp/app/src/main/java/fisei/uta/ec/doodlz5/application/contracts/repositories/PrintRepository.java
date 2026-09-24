package fisei.uta.ec.doodlz5.application.contracts.repositories;

import android.content.Context;
import android.graphics.Bitmap;

public interface PrintRepository {
    boolean printImage(Context context, Bitmap bitmap, String jobName);
}
