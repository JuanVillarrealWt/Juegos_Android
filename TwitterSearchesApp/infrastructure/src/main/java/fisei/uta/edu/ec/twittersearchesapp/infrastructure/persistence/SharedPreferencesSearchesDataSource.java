package fisei.uta.edu.ec.twittersearchesapp.infrastructure.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashMap;
import java.util.Map;

/** Low-level persistence operations for saved searches. */
public final class SharedPreferencesSearchesDataSource {
    private static final String PREFERENCES_NAME = "searches";
    private final SharedPreferences preferences;

    public SharedPreferencesSearchesDataSource(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static SharedPreferencesSearchesDataSource from(Context context) {
        return new SharedPreferencesSearchesDataSource(context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE));
    }

    public Map<String, String> getAll() {
        Map<String, String> searches = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            if (entry.getValue() instanceof String) {
                searches.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return searches;
    }

    public void save(String tag, String query) {
        preferences.edit().putString(tag, query).apply();
    }

    public void delete(String tag) {
        preferences.edit().remove(tag).apply();
    }
}
