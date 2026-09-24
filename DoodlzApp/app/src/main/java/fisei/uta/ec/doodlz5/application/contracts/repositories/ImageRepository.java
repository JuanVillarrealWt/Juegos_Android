package fisei.uta.ec.doodlz5.application.contracts.repositories;

import android.content.ContentResolver;
import android.graphics.Bitmap;

public interface ImageRepository {
    boolean saveImage(ContentResolver contentResolver, Bitmap bitmap, String name, String description);
}
