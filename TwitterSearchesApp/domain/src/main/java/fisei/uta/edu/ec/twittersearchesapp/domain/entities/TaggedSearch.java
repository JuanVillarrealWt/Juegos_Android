package fisei.uta.edu.ec.twittersearchesapp.domain.entities;

/** A saved search identified by a user supplied tag. */
public final class TaggedSearch {
    private final String tag;
    private final String query;

    public TaggedSearch(String tag, String query) {
        this.tag = tag;
        this.query = query;
    }

    public String getTag() { return tag; }
    public String getQuery() { return query; }
}
