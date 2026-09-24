package fisei.uta.edu.ec.flagquiz.presentation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fisei.uta.edu.ec.flagquiz.R;
import fisei.uta.edu.ec.flagquiz.application.contracts.QuizRepository;
import fisei.uta.edu.ec.flagquiz.application.usecases.GetQuizQuestionsUseCase;
import fisei.uta.edu.ec.flagquiz.composition.AppContainer;

public class MainActivityFragment extends Fragment {

    private static final String TAG = "FlagQuiz Activity";
    private static final int FLAGS_IN_QUIZ = 10;

    private List<String> fileNameList;
    private List<String> quizCountriesList;
    private Set<String> regionsSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private SecureRandom random;
    private Handler handler;
    private Animation shakeAnimation;

    private LinearLayout quizLinearLayout;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;

    private QuizRepository quizRepository;
    private GetQuizQuestionsUseCase getQuizQuestionsUseCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        if (requireActivity().getAssets() != null) {
            AppContainer appContainer = new AppContainer(requireActivity().getAssets());
            quizRepository = appContainer.getQuizRepository();
            getQuizQuestionsUseCase = appContainer.getGetQuizQuestionsUseCase();
        }

        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        flagImageView = view.findViewById(R.id.flagImageView);

        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);

        answerTextView = view.findViewById(R.id.answerTextView);

        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(
                getString(R.string.question, 1, FLAGS_IN_QUIZ));

        return view;
    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {
        String choices = sharedPreferences.getString(MainActivity.CHOICES, "4");
        if (choices != null) {
            guessRows = Integer.parseInt(choices) / 2;
        }

        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }
    }

    public void updateRegions(SharedPreferences sharedPreferences) {
        regionsSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
    }

    public void resetQuiz() {
        if (quizRepository == null || getQuizQuestionsUseCase == null) {
            return;
        }

        fileNameList.clear();
        fileNameList.addAll(quizRepository.getFileNamesForRegions(regionsSet));

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();

        quizCountriesList.addAll(getQuizQuestionsUseCase.generateQuizCountries(regionsSet, FLAGS_IN_QUIZ));

        loadNextFlag();
    }

    private void loadNextFlag() {
        if (quizCountriesList.isEmpty()) return;

        String nextImage = quizCountriesList.remove(0);
        correctAnswer = nextImage;
        answerTextView.setText("");

        questionNumberTextView.setText(
                getString(R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

        String region = nextImage.substring(0, nextImage.indexOf('-'));

        try (InputStream stream = quizRepository.getFlagStream(region, nextImage)) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);

            animate(false);
        } catch (IOException exception) {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList, random);

        int correct = fileNameList.indexOf(correctAnswer);
        if (correct != -1) {
            fileNameList.add(fileNameList.remove(correct));
        }

        for (int row = 0; row < guessRows; row++) {
            for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(quizRepository.getCountryName(filename));
            }
        }

        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        String countryName = quizRepository.getCountryName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(countryName);
    }

    private void animate(boolean animateOut) {
        if (correctAnswers == 0)
            return;

        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());

        Animator animator;

        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, radius, 0);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag();
                }
            });
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500);
        animator.start();
    }

    private final View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = (Button) v;
            String guess = guessButton.getText().toString();
            String answer = quizRepository.getCountryName(correctAnswer);
            ++totalGuesses;

            if (guess.equals(answer)) {
                ++correctAnswers;

                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                disableButtons();

                if (correctAnswers == FLAGS_IN_QUIZ) {
                    double score = (double) correctAnswers / totalGuesses * 100;

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.results, totalGuesses, score))
                            .setCancelable(false)
                            .setPositiveButton(R.string.reset_quiz,
                                    (dialog, id) -> resetQuiz());

                    AlertDialog resultsDialog = builder.create();
                    resultsDialog.show();
                } else {
                    handler.postDelayed(() -> animate(true), 2000);
                }
            } else {
                flagImageView.startAnimation(shakeAnimation);

                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(
                        getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));

                guessButton.setEnabled(false);
            }
        }
    };

    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++) {
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }
}
