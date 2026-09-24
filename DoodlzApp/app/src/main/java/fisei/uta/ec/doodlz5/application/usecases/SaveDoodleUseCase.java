package fisei.uta.ec.doodlz5.application.usecases;

import android.content.ContentResolver;
import android.graphics.Bitmap;

import fisei.uta.ec.doodlz5.application.contracts.repositories.ImageRepository;

public class SaveDoodleUseCase {
    private final ImageRepository imageRepository;

    public SaveDoodleUseCase(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public boolean execute(ContentResolver contentResolver, Bitmap bitmap, String name, String description) {
        if (imageRepository == null || bitmap == null) {
            return false;
        }
        return imageRepository.saveImage(contentResolver, bitmap, name, description);
    }
}
