package com.example.rajani.myquizapp;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity {

    //Questions and answers are stored in String arrays
    private String questionList[];
    private String answerList[];

    //Different layouts for different types of questions, i.e., single choice, multiple choice, and written answers
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

    //Timer - maximum time for a questions is 30 seconds
    private Chronometer chmTimer;

    //For single choice questions
    private RadioGroup rgChoicesGroup;
    private RadioButton rbChoice1;
    private RadioButton rbChoice2;
    private RadioButton rbChoice3;

    //For multiple choice questions
    private CheckBox cbChoice1;
    private CheckBox cbChoice2;
    private CheckBox cbChoice3;

    //For written answer questions
    private EditText etxtWritten;

    //Image for displaying whether a question has been answered correctly or not
    private ImageView singleChoiceAnswer;
    private ImageView multipleChoiceAnswer;
    private ImageView writtenTextAnswer;

    private int questionNumber = 1;
    private String questionCategory = "";
    private int totalScore = 0;
    private long orientationOffset;
    private boolean isTimerRunning;
    private boolean rightAnswer = false;

    //For saving state during orientation change
    private String QUESTION_NUMBER = "QUESTION_NUMBER";
    private String QUESTION_CATEGORY = "QUESTION_CATEGORY";
    private String TOTAL_SCORE = "TOTAL_SCORE";
    private String TIMER_OFFSET = "TIMER_OFFSET";
    private String TIMER_STATE = "TIMER_STATE";
    private String SUBMIT_BTN_STATE = "SUBMIT_BTN_STATE";
    private String SINGLE_CHOICE_1 = "SINGLE_CHOICE_1";
    private String SINGLE_CHOICE_2 = "SINGLE_CHOICE_2";
    private String SINGLE_CHOICE_3 = "SINGLE_CHOICE_3";
    private String MULTIPLE_CHOICE_1 = "MULTIPLE_CHOICE_1";
    private String MULTIPLE_CHOICE_2 = "MULTIPLE_CHOICE_2";
    private String MULTIPLE_CHOICE_3 = "MULTIPLE_CHOICE_3";
    private String WRITTEN_ANSWER = "WRITTEN_ANSWER";
    private String RESULT_MESSAGE = "RESULT_MESSAGE";
    private String IMAGE_VISIBILITY = "IMAGE_VISIBILITY";
    private String IMAGE_ID = "IMAGE_ID";
    private String FINAL_SCORE = "FINAL_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To prevent the soft keyboard from pushing up the layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Questions and answers in String arrays
        questionList = getResources().getStringArray(R.array.questions);
        answerList = getResources().getStringArray(R.array.answers);

        //Initialize all layouts
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

        //Initialize timer
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

        //Load first question
        loadQuestion(questionList[0], answerList[0]);
        chmTimer.start();
        isTimerRunning = true;

        //Add listeners to buttons and timer
        addListeners();
    }

    //Loads question along with appropriate layout
    private void loadQuestion(String question, String answers) {
        String[] splitQuestionItem = question.split(";");
        String[] splitAnswerItem = answers.split(";");
        loadAppropriateLayout(splitQuestionItem[1]);
        loadAppropriateQuestion(splitQuestionItem, splitAnswerItem);
    }

    private void addListeners() {
        //Timer stops and next question is loaded after lapse of 30 seconds
        chmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chmTimer.getBase()) >= 30000) {
                    chmTimer.stop();
                    isTimerRunning = false;
                    chmTimer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(MainActivity.this, "Oops! Your time is up! Next Question", Toast.LENGTH_SHORT).show();
                    nextStep();
                }
            }
        });

        //Submits selected answer for evaluation and stops the timer
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
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        } else if (questionNumber == 3) {
                            if (selectedId == R.id.rb_option2) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice2.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        } else if (questionNumber == 5) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        } else if (questionNumber == 7) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                singleChoiceAnswer.setVisibility(View.VISIBLE);
                                singleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        break;
                    case "mcq":
                        if (questionNumber == 1) {
                            if ((cbChoice2.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct1 = cbChoice2.getText().toString().trim();
                                String correct2 = cbChoice3.getText().toString().trim();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 4) {
                            if ((cbChoice1.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct1 = cbChoice1.getText().toString().trim();
                                String correct2 = cbChoice3.getText().toString().trim();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 9) {
                            if ((cbChoice2.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String correct1 = cbChoice2.getText().toString().trim();
                                String correct2 = cbChoice3.getText().toString().trim();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                multipleChoiceAnswer.setVisibility(View.VISIBLE);
                                multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
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
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 8) {
                            String correctAnswer = answerList[7].trim();
                            if (typedAnswer.compareToIgnoreCase(correctAnswer) == 0) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        if (questionNumber == 10) {
                            String correctAnswer = answerList[9].trim();
                            if (typedAnswer.compareToIgnoreCase(correctAnswer) == 0) {
                                txtMessageText.setText(R.string.correct);
                                Toast.makeText(MainActivity.this, R.string.correct, Toast.LENGTH_SHORT).show();
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.right);
                                rightAnswer = true;
                                totalScore++;
                                btnSubmitAnswer.setEnabled(false);
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
                                writtenTextAnswer.setVisibility(View.VISIBLE);
                                writtenTextAnswer.setImageResource(R.drawable.wrong);
                                rightAnswer = false;
                                txtMessageText.setText(formatted);
                                btnSubmitAnswer.setEnabled(false);
                            }
                        }
                        break;
                }
            }
        });

        //Stops timer and calls the method that loads the next question
        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chmTimer.stop();
                isTimerRunning = false;
                nextStep();
            }
        });

        //For exiting the app after viewing final score
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //Method is called when Next button is clicked as well as when the timer strikes 30 seconds
    private void nextStep() {
        if (questionNumber == 10) {
            String formatted;
            if (totalScore == 0) {
                formatted = getString(R.string.final_score_zero, totalScore, totalScore);
            } else {
                formatted = getResources().getQuantityString(R.plurals.final_score, totalScore, totalScore, totalScore);
            }
            chmTimer.stop();
            isTimerRunning = false;
            loadAppropriateLayout("score");
            txtFinalScore.setText(formatted);
            txtQuestionNumber.setText("");
            btnSubmitAnswer.setVisibility(View.INVISIBLE);
            btnNextQuestion.setVisibility(View.INVISIBLE);
            chmTimer.setVisibility(View.INVISIBLE);
            btnExit.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, formatted, Toast.LENGTH_SHORT).show();
        } else {
            questionNumber += 1;
            loadQuestion(questionList[questionNumber - 1], answerList[questionNumber - 1]);
            btnSubmitAnswer.setEnabled(true);
            chmTimer.setBase(SystemClock.elapsedRealtime());
            chmTimer.start();
            isTimerRunning = true;
        }
    }

    /*
     *Loads appropriate layout according to the category of question
     *Category scq is for single choice question
     *Category mcq is for multiple choice question
     *Category wrt is for written answer type question
     *Category score is for display of final score
     * */
    private void loadAppropriateLayout(String questionType) {
        questionCategory = questionType.trim();
        txtMessageText.setText("");
        switch (questionCategory) {
            case "scq":
                multipleChoiceLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.GONE);
                finalScoreLayout.setVisibility(View.GONE);
                singleChoiceLayout.setVisibility(View.VISIBLE);
                rgChoicesGroup.clearCheck();
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

    //Loads appropriate question according to the serial number of the question
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
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(QUESTION_NUMBER, questionNumber);
        outState.putString(QUESTION_CATEGORY, questionCategory);
        outState.putInt(TOTAL_SCORE, totalScore);
        if (isTimerRunning) {
            orientationOffset = SystemClock.elapsedRealtime() - chmTimer.getBase();
        }
        outState.putLong(TIMER_OFFSET, orientationOffset);
        outState.putBoolean(TIMER_STATE, isTimerRunning);
        outState.putBoolean(SUBMIT_BTN_STATE, btnSubmitAnswer.isEnabled());
        switch (questionCategory) {
            case "scq":
                outState.putBoolean(SINGLE_CHOICE_1, rbChoice1.isChecked());
                outState.putBoolean(SINGLE_CHOICE_2, rbChoice2.isChecked());
                outState.putBoolean(SINGLE_CHOICE_3, rbChoice3.isChecked());
                outState.putInt(IMAGE_VISIBILITY, singleChoiceAnswer.getVisibility());
                if (singleChoiceAnswer.getVisibility() == View.VISIBLE) {
                    outState.putBoolean(IMAGE_ID, rightAnswer);
                }
                break;
            case "mcq":
                outState.putBoolean(MULTIPLE_CHOICE_1, cbChoice1.isChecked());
                outState.putBoolean(MULTIPLE_CHOICE_2, cbChoice2.isChecked());
                outState.putBoolean(MULTIPLE_CHOICE_3, cbChoice3.isChecked());
                outState.putInt(IMAGE_VISIBILITY, multipleChoiceAnswer.getVisibility());
                if (multipleChoiceAnswer.getVisibility() == View.VISIBLE) {
                    outState.putBoolean(IMAGE_ID, rightAnswer);
                }
                break;
            case "wrt":
                outState.putString(WRITTEN_ANSWER, etxtWritten.getText().toString());
                outState.putInt(IMAGE_VISIBILITY, writtenTextAnswer.getVisibility());
                if (writtenTextAnswer.getVisibility() == View.VISIBLE) {
                    outState.putBoolean(IMAGE_ID, rightAnswer);
                }
                break;
            case "score":
                outState.putString(FINAL_SCORE, txtFinalScore.getText().toString());
                break;
        }
        outState.putString(RESULT_MESSAGE, txtMessageText.getText().toString());
    }

    /**
     * Retrieves data after orientation change
     */
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        questionNumber = savedInstanceState.getInt(QUESTION_NUMBER);
        questionCategory = savedInstanceState.getString(QUESTION_CATEGORY);
        totalScore = savedInstanceState.getInt(TOTAL_SCORE);
        orientationOffset = savedInstanceState.getLong(TIMER_OFFSET);
        isTimerRunning = savedInstanceState.getBoolean(TIMER_STATE);
        btnSubmitAnswer.setEnabled(savedInstanceState.getBoolean(SUBMIT_BTN_STATE));
        if (questionCategory.equals("score")) loadAppropriateLayout("score");
        else loadQuestion(questionList[questionNumber - 1], answerList[questionNumber - 1]);
        if (isTimerRunning) chmTimer.setBase(SystemClock.elapsedRealtime() - orientationOffset);
        else {
            chmTimer.setBase(SystemClock.elapsedRealtime() - orientationOffset);
            chmTimer.stop();
        }
        switch (questionCategory) {
            case "scq":
                rgChoicesGroup.clearCheck();
                rbChoice1.setChecked(savedInstanceState.getBoolean(SINGLE_CHOICE_1));
                rbChoice2.setChecked(savedInstanceState.getBoolean(SINGLE_CHOICE_2));
                rbChoice3.setChecked(savedInstanceState.getBoolean(SINGLE_CHOICE_3));
                singleChoiceAnswer.setVisibility(savedInstanceState.getInt(IMAGE_VISIBILITY));
                if (singleChoiceAnswer.getVisibility() == View.VISIBLE) {
                    if (savedInstanceState.getBoolean(IMAGE_ID)) {
                        singleChoiceAnswer.setImageResource(R.drawable.right);
                    } else {
                        singleChoiceAnswer.setImageResource(R.drawable.wrong);
                    }
                }
                break;
            case "mcq":
                cbChoice1.setChecked(savedInstanceState.getBoolean(MULTIPLE_CHOICE_1));
                cbChoice2.setChecked(savedInstanceState.getBoolean(MULTIPLE_CHOICE_2));
                cbChoice3.setChecked(savedInstanceState.getBoolean(MULTIPLE_CHOICE_3));
                multipleChoiceAnswer.setVisibility(savedInstanceState.getInt(IMAGE_VISIBILITY));
                if (multipleChoiceAnswer.getVisibility() == View.VISIBLE) {
                    if (savedInstanceState.getBoolean(IMAGE_ID)) {
                        multipleChoiceAnswer.setImageResource(R.drawable.right);
                    } else {
                        multipleChoiceAnswer.setImageResource(R.drawable.wrong);
                    }
                }
                break;
            case "wrt":
                etxtWritten.setText(savedInstanceState.getString(WRITTEN_ANSWER));
                writtenTextAnswer.setVisibility(savedInstanceState.getInt(IMAGE_VISIBILITY));
                if (writtenTextAnswer.getVisibility() == View.VISIBLE) {
                    if (savedInstanceState.getBoolean(IMAGE_ID)) {
                        writtenTextAnswer.setImageResource(R.drawable.right);
                    } else {
                        writtenTextAnswer.setImageResource(R.drawable.wrong);
                    }
                }
                break;
            case "score":
                txtFinalScore.setText(savedInstanceState.getString(FINAL_SCORE));
                break;
        }
        txtMessageText.setText(savedInstanceState.getString(RESULT_MESSAGE));
    }
}
