package fisei.uta.edu.ec.twittersearchesapp.infrastructure.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fisei.uta.edu.ec.twittersearchesapp.application.contracts.SearchesRepository;
import fisei.uta.edu.ec.twittersearchesapp.domain.entities.TaggedSearch;
import fisei.uta.edu.ec.twittersearchesapp.infrastructure.persistence.SharedPreferencesSearchesDataSource;

/** Maps persisted preference entries to the application repository contract. */
public final class SharedPreferencesSearchesRepository implements SearchesRepository {
    private final SharedPreferencesSearchesDataSource dataSource;

    public SharedPreferencesSearchesRepository(SharedPreferencesSearchesDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<TaggedSearch> getAll() {
        List<TaggedSearch> searches = new ArrayList<>();
        for (Map.Entry<String, String> entry : dataSource.getAll().entrySet()) {
            searches.add(new TaggedSearch(entry.getKey(), entry.getValue()));
        }
        return searches;
    }

    @Override
    public void save(TaggedSearch search) {
        dataSource.save(search.getTag(), search.getQuery());
    }

    @Override
    public void delete(String tag) {
        dataSource.delete(tag);
    }
}
