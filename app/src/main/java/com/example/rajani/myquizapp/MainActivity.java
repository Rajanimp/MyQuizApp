package com.example.rajani.myquizapp;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String questionList[];
    private String answerList[];

    private RelativeLayout singleChoiceLayout;
    private RelativeLayout multipleChoiceLayout;
    private RelativeLayout writtenAnswerLayout;
    private RelativeLayout finalScoreLayout;

    private Button btnSubmitAnswer;
    private Button btnNextQuestion;
    private Button btnExit;

    private TextView txtSingleChoiceQuestion;
    private TextView txtMultipleChoiceQuestion;
    private TextView txtWrittenAnswerQuestion;
    private TextView txtMessageText;
    private TextView txtQuestionNumber;
    private TextView txtFinalScore;

    private Chronometer chmTimer;

    private RadioGroup rgChoicesGroup;
    private RadioButton rbChoice1;
    private RadioButton rbChoice2;
    private RadioButton rbChoice3;

    private CheckBox cbChoice1;
    private CheckBox cbChoice2;
    private CheckBox cbChoice3;

    private EditText etxtWritten;

    private ImageView singleChoiceAnswer;
    private ImageView multipleChoiceAnswer;
    private ImageView writtenTextAnswer;

    private int questionNumber = 1;
    private String questionCategory = "";
    private int totalScore = 0;
    private long orientationOffset;
    private boolean isTimerRunning;

    private String QUESTION_NUMBER = "QUESTION_NUMBER";
    private String QUESTION_CATEGORY = "QUESTION_CATEGORY";
    private String TOTAL_SCORE = "TOTAL_SCORE";
    private String TIMER_OFFSET = "TIMER_OFFSET";
    private String TIMER_STATE = "TIMER_STATE";
    private String SUBMIT_BTN_STATE = "SUBMIT_BTN_STATE";
    private String SINGLE_CHOICE = "SINGLE_CHOICE";
    private String MULTIPLE_CHOICE_1 = "MULTIPLE_CHOICE_1";
    private String MULTIPLE_CHOICE_2 = "MULTIPLE_CHOICE_2";
    private String MULTIPLE_CHOICE_3 = "MULTIPLE_CHOICE_3";
    private String WRITTEN_ANSWER = "WRITTEN_ANSWER";
    private String FINAL_SCORE = "FINAL_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To prevent the soft keyboard from pushing up the layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        questionList = getResources().getStringArray(R.array.questions);
        answerList = getResources().getStringArray(R.array.answers);

        singleChoiceLayout = findViewById(R.id.rel_layout_radio);
        multipleChoiceLayout = findViewById(R.id.rel_layout_checkbox);
        writtenAnswerLayout = findViewById(R.id.rel_layout_etxt);
        finalScoreLayout = findViewById(R.id.rel_layout_final);

        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
        btnNextQuestion = findViewById(R.id.btn_next_question);
        btnExit = findViewById(R.id.btn_exit);

        txtMessageText = findViewById(R.id.tv_message);
        txtQuestionNumber = findViewById(R.id.tv_question_number);
        txtSingleChoiceQuestion = findViewById(R.id.single_choice_question);
        txtMultipleChoiceQuestion = findViewById(R.id.multiple_choice_question);
        txtWrittenAnswerQuestion = findViewById(R.id.written_answer_question);

        chmTimer = findViewById(R.id.chrono_timer);
        chmTimer.setBase(SystemClock.elapsedRealtime() - orientationOffset);

        rgChoicesGroup = findViewById(R.id.radio_group);
        rbChoice1 = findViewById(R.id.rb_option1);
        rbChoice2 = findViewById(R.id.rb_option2);
        rbChoice3 = findViewById(R.id.rb_option3);

        cbChoice1 = findViewById(R.id.cb_option1);
        cbChoice2 = findViewById(R.id.cb_option2);
        cbChoice3 = findViewById(R.id.cb_option3);

        etxtWritten = findViewById(R.id.etxt_written_answer);

        multipleChoiceAnswer = findViewById(R.id.img_checkbox);
        singleChoiceAnswer = findViewById(R.id.img_radio);
        writtenTextAnswer = findViewById(R.id.img_etxt);

        txtFinalScore = findViewById(R.id.tv_final_score_text);

        loadQuestion(questionList[0], answerList[0]);
        chmTimer.start();
        isTimerRunning = true;

        addListeners();
    }

    private void loadQuestion(String question, String answers) {
        String[] splitQuestionItem = question.split(";");
        String[] splitAnswerItem = answers.split(";");
        loadAppropriateLayout(splitQuestionItem[1]);
        loadAppropriateQuestion(splitQuestionItem, splitAnswerItem);
    }

    private void addListeners() {
        chmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if((SystemClock.elapsedRealtime() - chmTimer.getBase()) >= 30000){
                    chmTimer.stop();
                    orientationOffset = SystemClock.elapsedRealtime() - chmTimer.getBase();
                    isTimerRunning = false;
                    chmTimer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(MainActivity.this, "Oops! Your time is up! Next Question", Toast.LENGTH_SHORT).show();
                    nextStep();
                }
            }
        });
        btnSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chmTimer.stop();
                orientationOffset = SystemClock.elapsedRealtime() - chmTimer.getBase();
                isTimerRunning = false;
                switch (questionCategory) {
                    case "scq":
                        int selectedId = rgChoicesGroup.getCheckedRadioButtonId();
                        if (questionNumber == 2) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        } else if (questionNumber == 3) {
                            if (selectedId == R.id.rb_option2) {
                                txtMessageText.setText(R.string.correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice2.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        } else if (questionNumber == 5) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        } else if (questionNumber == 7) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        break;
                    case "mcq":
                        if (questionNumber == 1) {
                            if ((cbChoice2.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct1 = cbChoice2.getText().toString().trim();
                                String correct2 = cbChoice3.getText().toString().trim();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 4) {
                            if ((cbChoice1.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct1 = cbChoice1.getText().toString().trim();
                                String correct2 = cbChoice3.getText().toString().trim();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 9) {
                            if ((cbChoice2.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct1 = cbChoice2.getText().toString().trim();
                                String correct2 = cbChoice3.getText().toString().trim();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        break;
                    case "wrt":
                        String typedAnswer = etxtWritten.getText().toString();
                        if (questionNumber == 6) {
                            String correctAnswer = answerList[5].trim();
                            if (typedAnswer.compareToIgnoreCase(correctAnswer) == 0) {
                                txtMessageText.setText(R.string.correct);
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 8) {
                            String correctAnswer = answerList[7].trim();
                            if (typedAnswer.compareToIgnoreCase(correctAnswer) == 0) {
                                txtMessageText.setText(R.string.correct);
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 10) {
                            String correctAnswer = answerList[9].trim();
                            if (typedAnswer.compareToIgnoreCase(correctAnswer) == 0) {
                                txtMessageText.setText(R.string.correct);
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.right);
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.wrong);
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        break;
                }
            }
        });

        btnNextQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                chmTimer.stop();
                orientationOffset = SystemClock.elapsedRealtime() - chmTimer.getBase();
                isTimerRunning = false;
                nextStep();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void nextStep() {
        if(questionNumber == 10){
            String formatted;
            if(totalScore == 0){
                formatted = getString(R.string.final_score_zero, totalScore, totalScore);
            }else{
                formatted = getResources().getQuantityString(R.plurals.final_score, totalScore, totalScore, totalScore);
            }
            chmTimer.stop();
            orientationOffset = SystemClock.elapsedRealtime() - chmTimer.getBase();
            isTimerRunning = false;
            loadAppropriateLayout("score");
            txtFinalScore.setText(formatted);
            txtQuestionNumber.setText("");
            btnSubmitAnswer.setVisibility(View.INVISIBLE);
            btnNextQuestion.setVisibility(View.INVISIBLE);
            chmTimer.setVisibility(View.INVISIBLE);
            btnExit.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
        } else{
            questionNumber += 1;
            loadQuestion(questionList[questionNumber - 1], answerList[questionNumber - 1]);
            btnSubmitAnswer.setEnabled(true);
            chmTimer.setBase(SystemClock.elapsedRealtime());
            chmTimer.start();
            isTimerRunning = true;
        }
    }

    private void loadAppropriateLayout(String questionType) {
        questionCategory = questionType.trim();
        txtMessageText.setText("");
        switch (questionCategory) {
            case "scq":
                multipleChoiceLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.GONE);
                finalScoreLayout.setVisibility(View.GONE);
                singleChoiceLayout.setVisibility(View.VISIBLE);
                rbChoice1.setChecked(false);
                rbChoice2.setChecked(false);
                rbChoice3.setChecked(false);
                singleChoiceAnswer.setVisibility(View.INVISIBLE);
                break;
            case "mcq":
                singleChoiceLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.GONE);
                finalScoreLayout.setVisibility(View.GONE);
                multipleChoiceLayout.setVisibility(View.VISIBLE);
                cbChoice1.setChecked(false);
                cbChoice2.setChecked(false);
                cbChoice3.setChecked(false);
                multipleChoiceAnswer.setVisibility(View.INVISIBLE);
                break;
            case "wrt":
                singleChoiceLayout.setVisibility(View.GONE);
                multipleChoiceLayout.setVisibility(View.GONE);
                finalScoreLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.VISIBLE);
                writtenTextAnswer.setVisibility(View.INVISIBLE);
                etxtWritten.setText("");
                break;
            case "score":
                singleChoiceLayout.setVisibility(View.GONE);
                multipleChoiceLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.GONE);
                finalScoreLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadAppropriateQuestion(String[] splitQuestionItem, String[] splitAnswerItem) {
        String formatted = getString(R.string.question_number_display, questionNumber);
        txtQuestionNumber.setText(formatted);
        switch (splitQuestionItem[1].trim()) {
            case "scq":
                txtSingleChoiceQuestion.setText(splitQuestionItem[0]);
                rbChoice1.setText(splitAnswerItem[0]);
                rbChoice2.setText(splitAnswerItem[1]);
                rbChoice3.setText(splitAnswerItem[2]);
                break;
            case "mcq":
                txtMultipleChoiceQuestion.setText(splitQuestionItem[0]);
                cbChoice1.setText(splitAnswerItem[0]);
                cbChoice2.setText(splitAnswerItem[1]);
                cbChoice3.setText(splitAnswerItem[2]);
                break;
            case "wrt":
                txtWrittenAnswerQuestion.setText(splitQuestionItem[0]);
                break;
        }
    }

    /**
     * Saves data before orientation change
     * */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(QUESTION_NUMBER, questionNumber);
        outState.putString(QUESTION_CATEGORY, questionCategory);
        outState.putInt(TOTAL_SCORE, totalScore);
        outState.putLong(TIMER_OFFSET, orientationOffset);
        outState.putBoolean(TIMER_STATE, isTimerRunning);
        outState.putBoolean(SUBMIT_BTN_STATE, btnSubmitAnswer.isEnabled());
        switch (questionCategory){
            case "scq":
                outState.putInt(SINGLE_CHOICE, rgChoicesGroup.getCheckedRadioButtonId());
                Log.e("scq", ""+rgChoicesGroup.getCheckedRadioButtonId());
                rgChoicesGroup.clearCheck();
                break;
            case "mcq":
                outState.putBoolean(MULTIPLE_CHOICE_1, cbChoice1.isChecked());
                Log.e("mcq", ""+cbChoice1.isChecked());
                outState.putBoolean(MULTIPLE_CHOICE_2, cbChoice2.isChecked());
                Log.e("mcq", ""+cbChoice2.isChecked());
                outState.putBoolean(MULTIPLE_CHOICE_3, cbChoice3.isChecked());
                Log.e("mcq", ""+cbChoice3.isChecked());
                break;
            case "wrt":
                outState.putString(WRITTEN_ANSWER, etxtWritten.getText().toString());
                Log.e("wrt", etxtWritten.getText().toString());
                break;
            case "score":
                outState.putString(FINAL_SCORE, txtFinalScore.getText().toString());
                Log.e("score", txtFinalScore.getText().toString());
                break;
        }
    }

    /**
     * Retrieves data after orientation change
     * */
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        questionNumber = savedInstanceState.getInt(QUESTION_NUMBER);
        questionCategory = savedInstanceState.getString(QUESTION_CATEGORY);
        totalScore = savedInstanceState.getInt(TOTAL_SCORE);
        orientationOffset = savedInstanceState.getLong(TIMER_OFFSET);
        isTimerRunning = savedInstanceState.getBoolean(TIMER_STATE);
        btnSubmitAnswer.setEnabled(savedInstanceState.getBoolean(SUBMIT_BTN_STATE));
        if(questionCategory.equals("score"))
            loadAppropriateLayout("score");
        else
            loadQuestion(questionList[questionNumber - 1], answerList[questionNumber - 1]);
        if(isTimerRunning)
            chmTimer.setBase(SystemClock.elapsedRealtime() - orientationOffset);
        else{
            chmTimer.setBase(SystemClock.elapsedRealtime() - orientationOffset);
            chmTimer.stop();
            orientationOffset = SystemClock.elapsedRealtime() - chmTimer.getBase();
            isTimerRunning = false;
        }
        switch (questionCategory){
            case "scq":
                rgChoicesGroup.post(new Runnable() {
                    @Override
                    public void run() {
                        rgChoicesGroup.check(savedInstanceState.getInt(SINGLE_CHOICE));
                    }
                });
                break;
            case "mcq":
                cbChoice1.setChecked(savedInstanceState.getBoolean(MULTIPLE_CHOICE_1));
                cbChoice2.setChecked(savedInstanceState.getBoolean(MULTIPLE_CHOICE_2));
                cbChoice3.setChecked(savedInstanceState.getBoolean(MULTIPLE_CHOICE_3));
                break;
            case "wrt":
                etxtWritten.setText(savedInstanceState.getString(WRITTEN_ANSWER));
                break;
            case "score":
                txtFinalScore.setText(savedInstanceState.getString(FINAL_SCORE));
                break;
        }
    }
}
