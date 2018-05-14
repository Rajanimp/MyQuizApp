package com.example.rajani.myquizapp;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String questionList[];
    String answerList[];

    RelativeLayout singleChoiceLayout;
    RelativeLayout multipleChoiceLayout;
    RelativeLayout writtenAnswerLayout;

    Button btnSubmitAnswer;
    Button btnNextQuestion;

    TextView txtSingleChoiceQuestion;
    TextView txtMultipleChoiceQuestion;
    TextView txtWrittenAnswerQuestion;
    TextView txtMessageText;
    TextView txtQuestionNumber;
    //TextView txtTimer;

    RadioGroup rgChoicesGroup;
    RadioButton rbChoice1;
    RadioButton rbChoice2;
    RadioButton rbChoice3;

    CheckBox cbChoice1;
    CheckBox cbChoice2;
    CheckBox cbChoice3;

    EditText etxtWritten;

    int questionNumber = 1;
    String questionCategory = "";
    int totalScore = 0;
    //int counter = 30;

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

        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
        btnNextQuestion = findViewById(R.id.btn_next_question);

        txtMessageText = findViewById(R.id.tv_message);
        txtQuestionNumber = findViewById(R.id.tv_question_number);
        //txtTimer = findViewById(R.id.tv_timer);
        txtSingleChoiceQuestion = findViewById(R.id.single_choice_question);
        txtMultipleChoiceQuestion = findViewById(R.id.multiple_choice_question);
        txtWrittenAnswerQuestion = findViewById(R.id.written_answer_question);

        rgChoicesGroup = findViewById(R.id.radio_group);
        rbChoice1 = findViewById(R.id.rb_option1);
        rbChoice2 = findViewById(R.id.rb_option2);
        rbChoice3 = findViewById(R.id.rb_option3);

        cbChoice1 = findViewById(R.id.cb_option1);
        cbChoice2 = findViewById(R.id.cb_option2);
        cbChoice3 = findViewById(R.id.cb_option3);

        etxtWritten = findViewById(R.id.etxt_written_answer);

        loadQuestion(questionList[0], answerList[0]);

        addListeners();
    }

    private void loadQuestion(String question, String answers) {
        String[] splitQuestionItem = question.split(";");
        String[] splitAnswerItem = answers.split(";");
        loadAppropriateLayout(splitQuestionItem[1]);
        loadAppropriateQuestion(splitQuestionItem, splitAnswerItem);
    }

    private void addListeners() {
        btnSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (questionCategory) {
                    case "scq":
                        int selectedId = rgChoicesGroup.getCheckedRadioButtonId();
                        if (questionNumber == 2) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                txtMessageText.setText(formatted);
                            }
                        } else if (questionNumber == 3) {
                            if (selectedId == R.id.rb_option2) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct = rbChoice2.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                txtMessageText.setText(formatted);
                            }
                        } else if (questionNumber == 5) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                txtMessageText.setText(formatted);
                            }
                        } else if (questionNumber == 7) {
                            if (selectedId == R.id.rb_option1) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct = rbChoice1.getText().toString();
                                String formatted = getString(R.string.incorrect, correct);
                                txtMessageText.setText(formatted);
                            }
                        }
                        break;
                    case "mcq":
                        if (questionNumber == 1) {
                            if ((cbChoice2.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct1 = cbChoice2.getText().toString();
                                String correct2 = cbChoice3.getText().toString();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                txtMessageText.setText(formatted);
                            }
                        }
                        if (questionNumber == 4) {
                            if ((cbChoice1.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct1 = cbChoice1.getText().toString();
                                String correct2 = cbChoice3.getText().toString();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                txtMessageText.setText(formatted);
                            }
                        }
                        if (questionNumber == 9) {
                            if ((cbChoice2.isChecked()) && (cbChoice3.isChecked())) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String correct1 = cbChoice2.getText().toString();
                                String correct2 = cbChoice3.getText().toString();
                                String formatted = getString(R.string.incorrect_mcq, correct1, correct2);
                                txtMessageText.setText(formatted);
                            }
                        }
                        break;
                    case "wrt":
                        String typedAnswer = etxtWritten.getText().toString();
                        if (questionNumber == 6) {
                            String correctAnswer = answerList[5].trim();
                            if (typedAnswer.equals(correctAnswer)) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                txtMessageText.setText(formatted);
                            }
                        }
                        if (questionNumber == 8) {
                            String correctAnswer = answerList[7].trim();
                            if (typedAnswer.equals(correctAnswer)) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                txtMessageText.setText(formatted);
                            }
                        }
                        if (questionNumber == 10) {
                            String correctAnswer = answerList[9].trim();
                            if (typedAnswer.equals(correctAnswer)) {
                                txtMessageText.setText(R.string.correct);
                                totalScore++;
                            } else {
                                String formatted = getString(R.string.incorrect, correctAnswer);
                                txtMessageText.setText(formatted);
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
                if(questionNumber == 10){
                    String formatted = getString(R.string.final_score, totalScore);
                    txtMessageText.setText(formatted);
                } else{
                    questionNumber += 1;
                    loadQuestion(questionList[questionNumber - 1], answerList[questionNumber - 1]);
                }
            }
        });
    }

    private void loadAppropriateLayout(String questionType) {
        questionCategory = questionType.trim();
        txtMessageText.setText("");
        switch (questionCategory) {
            case "scq":
                singleChoiceLayout.setVisibility(View.VISIBLE);
                rbChoice1.setChecked(false);
                rbChoice2.setChecked(false);
                rbChoice3.setChecked(false);
                multipleChoiceLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.GONE);
                break;
            case "mcq":
                singleChoiceLayout.setVisibility(View.GONE);
                multipleChoiceLayout.setVisibility(View.VISIBLE);
                cbChoice1.setChecked(false);
                cbChoice2.setChecked(false);
                cbChoice3.setChecked(false);
                writtenAnswerLayout.setVisibility(View.GONE);
                break;
            case "wrt":
                singleChoiceLayout.setVisibility(View.GONE);
                multipleChoiceLayout.setVisibility(View.GONE);
                writtenAnswerLayout.setVisibility(View.VISIBLE);
                etxtWritten.setText("");
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
}
