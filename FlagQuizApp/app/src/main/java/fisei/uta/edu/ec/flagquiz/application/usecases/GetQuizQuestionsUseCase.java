package fisei.uta.edu.ec.flagquiz.application.usecases;

import fisei.uta.edu.ec.flagquiz.application.contracts.QuizRepository;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GetQuizQuestionsUseCase {
    private final QuizRepository quizRepository;
    private final SecureRandom random = new SecureRandom();

    public GetQuizQuestionsUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public List<String> generateQuizCountries(Set<String> regionsSet, int flagsInQuiz) {
        List<String> fileNameList = quizRepository.getFileNamesForRegions(regionsSet);
        List<String> quizCountriesList = new ArrayList<>();

        if (fileNameList.isEmpty()) {
            return quizCountriesList;
        }

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();

        while (flagCounter <= flagsInQuiz && numberOfFlags > 0) {
            int randomIndex = random.nextInt(numberOfFlags);
            String filename = fileNameList.get(randomIndex);

            if (!quizCountriesList.contains(filename)) {
                quizCountriesList.add(filename);
                ++flagCounter;
            }
        }

        return quizCountriesList;
    }

    public List<String> getShuffledFileNameList(Set<String> regionsSet) {
        List<String> fileNameList = new ArrayList<>(quizRepository.getFileNamesForRegions(regionsSet));
        Collections.shuffle(fileNameList, random);
        return fileNameList;
    }
}
