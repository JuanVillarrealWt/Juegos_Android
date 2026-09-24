package fisei.uta.edu.ec.twittersearchesapp.application.contracts;

import java.util.List;

import fisei.uta.edu.ec.twittersearchesapp.domain.entities.TaggedSearch;

/** Storage contract for saved searches. */
public interface SearchesRepository {
    List<TaggedSearch> getAll();
    void save(TaggedSearch search);
    void delete(String tag);
}
