package com.example.quizapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


//Reference: https://www.youtube.com/watch?v=PiCZQg4mhBQ&list=PLrnPJCHvNZuDCyg4Usq2gHMzz6_CiyQO7


public class QuizActivity extends AppCompatActivity {

    private TextView textViewScore;
    private TextView textViewQuestionNum;
    private TextView textViewQuestion;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button btnNext;
    private ColorStateList textColorDefaultRb;
    private List<Question> questionList = new ArrayList<>();
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;
    private TextView textViewName;
    private Button btnFinish;
    private int score;
    private boolean answered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        textViewScore = findViewById(R.id.txtViewScore);
        Bundle b = getIntent().getExtras();
        textViewName = findViewById(R.id.txtViewName);
        textViewQuestionNum = findViewById(R.id.txtViewQuestionNo);
        textViewQuestion = findViewById(R.id.txtViewQuestion);
        rbGroup = findViewById(R.id.radiogrpOptions);
        rb1 = findViewById(R.id.radioBtnOption1);
        rb2 = findViewById(R.id.radioBtnOption2);
        rb3 = findViewById(R.id.radioBtnOption3);
        rb4 = findViewById(R.id.radioBtnOption4);
        btnNext = findViewById(R.id.btnNext);
        textColorDefaultRb = rb1.getTextColors();
        btnFinish = findViewById(R.id.btnFinish);

        try {

            //Get, validate and display user name
            String playerName = b.getString("username");
            textViewName.setText(playerName);

            if (!playerName.matches("^(\\s)*[A-Za-z]+((\\s)?((\\'|\\-|\\.)?([A-Za-z])+))*(\\s)*$"))
            {
                Toast.makeText(QuizActivity.this, "Please give your name (no special characters or numbers)", Toast.LENGTH_LONG).show();
                finishQuiz();
            }
            else {
                //Read questions and answers from the file (Questions.txt)
                readFile();
                questionCountTotal = questionList.size();
                //Shuffle the questions to get questions in a random order
                Collections.shuffle(questionList);

                //Display the next question
                showNextQuestion();

                //When the Next button is clicked, check the answer
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Check for selected answer
                        if (!answered) {
                            //If any of the 4 options ic checked then check the answer
                            if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                                checkAnswer();
                            } else {
                                Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //Display the next question
                            showNextQuestion();
                        }
                    }
                });

                btnFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishQuiz();
                    }
                });
            }

        } //end try

        catch (Exception e) {
            e.printStackTrace();
            Log.w("Quiz App",e);
        }

    }


    //Declare a method to read questions and answers from a file
    public void readFile()
    {
        HashMap<String, String> questionAndAnswerMap = new HashMap<String, String>();
        ArrayList<String[]> questionsAndAnswers = new ArrayList<>();
        ArrayList<String> questionOptions = new ArrayList<>();
        ArrayList<String> quesop = new ArrayList<>();
        String str="";

        try
        {
            InputStream inputStream = getAssets().open("Questions.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            str = new String(buffer);

            //Read a line unless a new line is found
            String[] lines = str.split("(\\r\\n|\\r|\\n)"); //Regex to match new line

            //Store the file content into a hashmap, questions as keys and answers as values
            for(int i=0;i<lines.length;i++)
            {
                //Split each line to seperate questions and answers
                String[] s = lines[i].split(":");

                questionAndAnswerMap.put(s[0], s[1]);

            }

            //Assign hashmap contents into 2 list, one for questions and answers and one for only answers
            Iterator<Map.Entry<String,String>> iter = questionAndAnswerMap.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String,String> entry = iter.next();

                int m=0, n=1;
                String k = entry.getKey();
                String v = entry.getValue();

                String[] strArray = new String[questionAndAnswerMap.size()];

                strArray[m] = k;
                strArray[n] = v;


                //Segregate Hashmap key and values and value into 2 separate list
               questionsAndAnswers.add(strArray);
               questionOptions.add(v);

            }//end while loop

            //Set the question options for a question
            for(int i=0;i<questionsAndAnswers.size();i++)
            {
                Question question = new Question();

                Collections.shuffle(questionOptions);

                quesop.add(questionsAndAnswers.get(i)[1]);

                for(int k=1;k<=3;k++)
                {
                    if(!questionsAndAnswers.get(i)[1].matches(questionOptions.get(k)))
                    {
                        quesop.add(questionOptions.get(k));
                    }
                    else
                    {
                        quesop.add(questionOptions.get(k+5));
                    }
                }

                //Shuffle the question options
                Collections.shuffle(quesop);

                //Set the question and options to an object of Question class
                question.setQuestion(questionsAndAnswers.get(i)[0]);
                question.setOption1(quesop.get(0));
                question.setOption2(quesop.get(1));
                question.setOption3(quesop.get(2));
                question.setOption4(quesop.get(3));

                //Set the correct answer for the question
                for(int j=0;j<=3;j++)
                {
                   if(questionsAndAnswers.get(i)[1].matches(quesop.get(j)))
                    {
                        question.setAnswerNum(j+1);
                        break;
                    }
                }

                //Add the question instance to the Question type list of questionList
                questionList.add(question);

                //Remove all elements from the list
                quesop.clear();

            }//end for loop

        }//end try

        catch (Exception e)
        {
            e.printStackTrace();
            Log.w("Quiz App",e);
        }
    }//end method

    //Display the question from the question list
    private void showNextQuestion()
    {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if(questionCounter<questionCountTotal)
        {
            currentQuestion = questionList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());
            questionCounter++;

            textViewQuestionNum.setText("Question: "+questionCounter+"/"+questionCountTotal);
            answered = false;
            btnNext.setText("Confirm");

        }
        else
        {
            //Terminate the activity
            finishQuiz();
        }
    }//end method

    //Declare the method to check answer
    private void checkAnswer()
    {
        answered = true;
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());

        //Get the selected radio button index to compare with the correct answer of the question
        int selectedOption = rbGroup.indexOfChild(rbSelected)+1;
        if(selectedOption == currentQuestion.getAnswerNum())
        {
            Toast.makeText(QuizActivity.this,"Correct",Toast.LENGTH_LONG).show();
            score++;
        }
        else
        {
            Toast.makeText(QuizActivity.this,"Incorrect",Toast.LENGTH_LONG).show();
        }

        showAnswer(selectedOption);

    }//end method

    //Declare the method to display the correct answer and change the radio button colors
    private void showAnswer(int selected)
    {
        if(selected == currentQuestion.getAnswerNum())
        {
            switch(selected)
            {
                case 1:
                    rb1.setTextColor(Color.GREEN);
                    break;
                case 2:
                    rb2.setTextColor(Color.GREEN);
                    break;
                case 3:
                    rb3.setTextColor(Color.GREEN);
                    break;
                case 4:
                    rb4.setTextColor(Color.GREEN);
                    break;
            }
        }
        else
        {
            switch(selected)
            {
                case 1:
                    rb1.setTextColor(Color.RED);
                    break;
                case 2:
                    rb2.setTextColor(Color.RED);
                    break;
                case 3:
                    rb3.setTextColor(Color.RED);
                    break;
                case 4:
                    rb4.setTextColor(Color.RED);
                    break;
            }
        }

        //Display the score
        textViewScore.setText("Score: "+score);

        //Set the navigation button for Next and Finish
        if(questionCounter<questionCountTotal)
        {
            btnNext.setText("Next");
        }
        else
        {
            btnNext.setText("Finish");
            btnFinish.setVisibility(View.GONE);
        }

    }//end method

    //Declare the method to close the activity
    private void finishQuiz()
    {
        Intent resultIntent = new Intent();
        //Send back the score before closing the activity
        resultIntent.putExtra("HighScore",Integer.toString(score));
        setResult(RESULT_OK,resultIntent);
        finish();

    }//end method

} //end QuizActivity

