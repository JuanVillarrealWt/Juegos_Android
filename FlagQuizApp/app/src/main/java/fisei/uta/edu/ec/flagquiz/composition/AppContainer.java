package fisei.uta.edu.ec.flagquiz.composition;

import android.content.res.AssetManager;
import fisei.uta.edu.ec.flagquiz.application.contracts.QuizRepository;
import fisei.uta.edu.ec.flagquiz.application.usecases.GetQuizQuestionsUseCase;
import fisei.uta.edu.ec.flagquiz.infrastructure.repositories.AssetQuizRepository;

public class AppContainer {
    private final AssetManager assetManager;
    private QuizRepository quizRepository;
    private GetQuizQuestionsUseCase getQuizQuestionsUseCase;

    public AppContainer(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public QuizRepository getQuizRepository() {
        if (quizRepository == null) {
            quizRepository = new AssetQuizRepository(assetManager);
        }
        return quizRepository;
    }

    public GetQuizQuestionsUseCase getGetQuizQuestionsUseCase() {
        if (getQuizQuestionsUseCase == null) {
            getQuizQuestionsUseCase = new GetQuizQuestionsUseCase(getQuizRepository());
        }
        return getQuizQuestionsUseCase;
    }
}
