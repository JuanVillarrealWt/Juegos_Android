package fisei.uta.edu.ec.twittersearchesapp.application.contracts;

import fisei.uta.edu.ec.twittersearchesapp.application.usecases.SearchesUseCases;

/** Implemented by the application module to provide presentation dependencies. */
public interface SearchesDependencies {
    SearchesUseCases getSearchesUseCases();
}
