package fisei.uta.ec.doodlz5.infrastructure.persistence;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import fisei.uta.ec.doodlz5.application.contracts.repositories.ImageRepository;

public class MediaStoreImageRepositoryImpl implements ImageRepository {
    @Override
    public boolean saveImage(ContentResolver contentResolver, Bitmap bitmap, String name, String description) {
        if (contentResolver == null || bitmap == null) {
            return false;
        }
        String location = MediaStore.Images.Media.insertImage(contentResolver, bitmap, name, description);
        return location != null;
    }
}
