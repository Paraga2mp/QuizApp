package com.example.quizapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String name="";
    private EditText EditName;
    private TextView textViewHighscore;
    private Button buttonStartQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditName = findViewById(R.id.txtEditName);
        buttonStartQuiz = findViewById(R.id.btnStartQuiz);
        textViewHighscore = findViewById(R.id.txtViewHighscore);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    //Method to get and display the highest score of the quiz
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            textViewHighscore.setText("Highscore: ".concat(data.getStringExtra("HighScore")));
        }
    }

    //Declare a method to start activity
    private void startQuiz()
    {
        Intent intent = new Intent(MainActivity.this,QuizActivity.class);
        name = EditName.getText().toString();
        Bundle b = new Bundle();
        b.putString("username",name);
        intent.putExtras(b);
        //Get the result back with the activity, here only 1 activity
        startActivityForResult(intent,1);
    }
}
