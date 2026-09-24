package fisei.uta.edu.ec.twittersearchesapp.application.usecases;

import java.util.List;

import fisei.uta.edu.ec.twittersearchesapp.application.contracts.SearchesRepository;
import fisei.uta.edu.ec.twittersearchesapp.domain.entities.TaggedSearch;

/** Application rules for managing saved searches. */
public final class SearchesUseCases {
    private final SearchesRepository repository;

    public SearchesUseCases(SearchesRepository repository) {
        this.repository = repository;
    }

    public List<TaggedSearch> getSavedSearches() {
        return repository.getAll();
    }

    public void saveSearch(String tag, String query) {
        String normalizedTag = tag == null ? "" : tag.trim();
        String normalizedQuery = query == null ? "" : query.trim();
        if (normalizedTag.isEmpty() || normalizedQuery.isEmpty()) {
            throw new IllegalArgumentException("Tag and query must not be empty");
        }
        repository.save(new TaggedSearch(normalizedTag, normalizedQuery));
    }

    public void deleteSearch(String tag) {
        repository.delete(tag);
    }
}
