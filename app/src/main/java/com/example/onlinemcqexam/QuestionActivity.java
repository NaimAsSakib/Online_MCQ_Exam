package com.example.onlinemcqexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinemcqexam.model.ApiInterface;
import com.example.onlinemcqexam.model.Response;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionActivity extends AppCompatActivity {
    final int totalNumberOfRadioButton = 4;//total number of RadioButtons
    RadioButton[] radioButton;  //Array of radioButtons
    RadioGroup radioGroup;

    String correctAns, wrongAns1, wrongAns2, wrongAns3;
    String radioButtonSelectedAnswer;

    TextView tvQuestion;

    AppCompatButton buttonNext, btnSubmit;

    String baseURL = "https://the-trivia-api.com/api/";
    ApiInterface apiInterface;
    String selectedCategory;
    String questionID;
    private int totalNumber;
    private MySharedPref mySharedPref;

    LoadingProgressBarDialog loadingProgressBarDialog;

    private boolean checkClick;
    int countClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mySharedPref = new MySharedPref(this);

        radioGroup = (RadioGroup) findViewById(R.id.radGroup);

        checkClick = false;

        loadingProgressBarDialog = new LoadingProgressBarDialog(this);

        btnSubmit = findViewById(R.id.btnSubmit);

        tvQuestion = findViewById(R.id.tvQuestionAct);
        radioGroup = findViewById(R.id.radGroup);
        buttonNext = findViewById(R.id.btnNext);

        //getting selected category from MainActivity
        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("categoryName");

        //Api code
        networkLibraryInitializer();
        getData();

        //method to check answer & passing marks
        checkingAnswer();
    }

    private void networkLibraryInitializer() {
        //Using OkHttp client to initialize
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)    //this is part of OkHttp
                .build();
        apiInterface = retrofit.create(ApiInterface.class);  //This line is for one API, We can use multiple API like these

    }


    private void getData() {
        loadingProgressBarDialog.startProgressBarLoading();  //progressBar

        Call<ArrayList<Response>> responseQuestions = apiInterface.getQuestions(selectedCategory, 1);
        responseQuestions.enqueue(new Callback<ArrayList<Response>>() {
            @Override
            public void onResponse(Call<ArrayList<Response>> call, retrofit2.Response<ArrayList<Response>> response) {
                ArrayList<Response> responses = response.body();

                //getting answers from API & keeping answers to String variables
                correctAns = responses.get(0).getCorrectAnswer();
                wrongAns1 = responses.get(0).getIncorrectAnswers().get(0);
                wrongAns2 = responses.get(0).getIncorrectAnswers().get(1);
                wrongAns3 = responses.get(0).getIncorrectAnswers().get(2);

                String[] options = {wrongAns1, wrongAns2, wrongAns3, correctAns}; //keeping all options in array

                //Initializing the RadioButtons
                radioButton = new RadioButton[totalNumberOfRadioButton];

                for (int i = 0; i < totalNumberOfRadioButton; i++) {
                    radioButton[i] = new RadioButton(QuestionActivity.this);

                    //options are being loaded here in radioButtons
                    radioButton[i].setText(options[i]);
                }


                //Random Swapping of the radioButtons
                for (int i = 0; i < 4; i++) {    //this loop is randomly changing values 4 times
                    int swap_ind1 = ((int) (Math.random() * 10) % totalNumberOfRadioButton);
                    int swap_ind2 = ((int) (Math.random() * 10) % totalNumberOfRadioButton);
                    RadioButton temp = radioButton[swap_ind1];
                    radioButton[swap_ind1] = radioButton[swap_ind2];
                    radioButton[swap_ind2] = temp;
                }

                //Adding RadioButtons in RadioGroup
                for (int i = 0; i < totalNumberOfRadioButton; i++) {
                    radioGroup.addView(radioButton[i]);
                }

                tvQuestion.setText(responses.get(0).getQuestion());  //setting question to textView

                // questionID = responses.get(0).getId();
                Toast.makeText(QuestionActivity.this, "Correct ans " + correctAns, Toast.LENGTH_SHORT).show();
                // Toast.makeText(QuestionActivity.this, "Response successful for Question act", Toast.LENGTH_SHORT).show();

                loadingProgressBarDialog.dismissProgressBarDialog();

            }

            @Override
            public void onFailure(Call<ArrayList<Response>> call, Throwable t) {
                Toast.makeText(QuestionActivity.this, "Response failed for question Act", Toast.LENGTH_SHORT).show();
                loadingProgressBarDialog.dismissProgressBarDialog();

            }
        });
    }

    private void checkingAnswer() {


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {

                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(id);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    // Get the text of selected radiobutton through checkedRadioButton
                    radioButtonSelectedAnswer = checkedRadioButton.getText().toString();

                }

            }
        });

        //condition for checking previously saved mark for correct ans
        if (mySharedPref.getInt("passingValue") != null) {
            totalNumber = mySharedPref.getInt("passingValue");

        } else {
            totalNumber = 0;
        }


        Log.e("Total number", " number " + totalNumber);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkClick = true;

                if (radioButtonSelectedAnswer != null) {
                    if (radioButtonSelectedAnswer.equals(correctAns) && checkClick) {
                        totalNumber = totalNumber + 5;
                        mySharedPref.putInt("passingValue", totalNumber);

                    }
                }

                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                startActivity(intent);

                Toast.makeText(QuestionActivity.this, "Total number " + totalNumber, Toast.LENGTH_SHORT).show();

                finish();

            }
        });


        //code for counting next button click, handling and passing data when next & submit buttons are clicked
       /* if (mySharedPref.getInt("countButtonClick") != null) {
            //Log.e("clicked value"," value "+mySharedPref.getInt("countButtonClick"));
            countClick = mySharedPref.getInt("countButtonClick");
            countClick = countClick + 1;
        } */

        //countClick is for checking total next buttonClick
        countClick = mySharedPref.getInt("countButtonClick");  //initially for first question it will start from 1 before clicking next button
        countClick = countClick + 1;
        mySharedPref.putInt("countButtonClick", countClick);
        Log.e("clicked value"," value "+countClick);

        //condition for max number of question
        if (mySharedPref.getInt("countButtonClick") == 5) {  //5 is the max number of question
            buttonNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalNumber = totalNumber + 5;
                mySharedPref.putInt("passingValue", totalNumber);
                Log.e("total number ", "btnSubmit "+totalNumber);
                Intent intent1 = new Intent(QuestionActivity.this, ResultActivity.class);
                intent1.putExtra("totalQuestion", countClick);
                startActivity(intent1);
            }
        });

        //timer to go to next question automatically after certain time
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if user click button it means checkClick is true, so timer will not work. if user doesn't click checkClick will be false it will perform button click auto
                if (!checkClick) {
                    buttonNext.performClick();
                }
            }
        }, 8000);*/
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        mySharedPref.clearData();
        startActivity(new Intent(QuestionActivity.this, MainActivity.class));
        finish();

        //Code to exit app instantly
        /*moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }

}