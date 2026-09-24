package fisei.uta.edu.ec.twittersearchesapp;

import android.app.Application;

import fisei.uta.edu.ec.twittersearchesapp.application.contracts.SearchesDependencies;
import fisei.uta.edu.ec.twittersearchesapp.application.usecases.SearchesUseCases;
import fisei.uta.edu.ec.twittersearchesapp.infrastructure.persistence.SharedPreferencesSearchesDataSource;
import fisei.uta.edu.ec.twittersearchesapp.infrastructure.repositories.SharedPreferencesSearchesRepository;

/** Composition root: binds Android persistence to domain use cases. */
public final class TwitterCompositionRoot extends Application implements SearchesDependencies {
    private SearchesUseCases searchesUseCases;

    @Override
    public void onCreate() {
        super.onCreate();
        searchesUseCases = new SearchesUseCases(new SharedPreferencesSearchesRepository(
                SharedPreferencesSearchesDataSource.from(this)));
    }

    @Override
    public SearchesUseCases getSearchesUseCases() {
        return searchesUseCases;
    }
}
