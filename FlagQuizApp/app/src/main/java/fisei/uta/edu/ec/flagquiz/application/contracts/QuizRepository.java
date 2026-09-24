package fisei.uta.edu.ec.flagquiz.application.contracts;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface QuizRepository {
    List<String> getFileNamesForRegions(Set<String> regions);
    InputStream getFlagStream(String region, String nextImage) throws IOException;
    String getCountryName(String fileName);
}
