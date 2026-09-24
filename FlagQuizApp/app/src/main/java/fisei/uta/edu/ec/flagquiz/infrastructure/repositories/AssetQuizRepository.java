package fisei.uta.edu.ec.flagquiz.infrastructure.repositories;

import android.content.res.AssetManager;
import android.util.Log;

import fisei.uta.edu.ec.flagquiz.application.contracts.QuizRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssetQuizRepository implements QuizRepository {
    private static final String TAG = "AssetQuizRepository";
    private final AssetManager assetManager;

    public AssetQuizRepository(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public List<String> getFileNamesForRegions(Set<String> regions) {
        List<String> fileNameList = new ArrayList<>();
        if (regions == null) {
            return fileNameList;
        }

        try {
            for (String region : regions) {
                String[] paths = assetManager.list(region);
                if (paths != null) {
                    for (String path : paths) {
                        fileNameList.add(path.replace(".png", ""));
                    }
                }
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }

        return fileNameList;
    }

    @Override
    public InputStream getFlagStream(String region, String nextImage) throws IOException {
        return assetManager.open(region + "/" + nextImage + ".png");
    }

    @Override
    public String getCountryName(String fileName) {
        return fileName.substring(fileName.indexOf('-') + 1).replace('_', ' ');
    }
}
