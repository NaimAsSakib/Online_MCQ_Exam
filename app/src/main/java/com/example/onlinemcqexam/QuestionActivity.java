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
    final int NUMBER_OF_RADIOBUTTONS_TO_ADD = 4;//Change it for other number of RadioButtons
    RadioButton[] radioButton;
    RadioGroup radioGroup;

    String correctAns, wrongAns1, wrongAns2, wrongAns3;
    String correctAnswer;
    String radioButtonSelectedAnswer;

    TextView textView;

    AppCompatButton buttonNext;

    String baseURL = "https://the-trivia-api.com/api/";
    ApiInterface apiInterface;
    String selectedCategory;
    String questionID;
    int totalNumber;
    MySharedPref mySharedPref;

    LoadingProgressBarDialog loadingProgressBarDialog;

    private boolean checkClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        radioGroup = (RadioGroup) findViewById(R.id.radGroup);

        checkClick = false;

        loadingProgressBarDialog = new LoadingProgressBarDialog(this);


        textView = findViewById(R.id.tvQuestionAct);
        radioGroup = findViewById(R.id.radGroup);
        buttonNext = findViewById(R.id.btnNext);


        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("categoryName");

        networkLibraryInitializer();
        getData();

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
        loadingProgressBarDialog.startProgressBarLoading();

        Call<ArrayList<Response>> responseQuestions = apiInterface.getQuestions(selectedCategory, 1);
        responseQuestions.enqueue(new Callback<ArrayList<Response>>() {
            @Override
            public void onResponse(Call<ArrayList<Response>> call, retrofit2.Response<ArrayList<Response>> response) {
                ArrayList<Response> responses = response.body();

                correctAns=responses.get(0).getCorrectAnswer();
                wrongAns1=responses.get(0).getIncorrectAnswers().get(0);
                wrongAns2=responses.get(0).getIncorrectAnswers().get(1);
                wrongAns3=responses.get(0).getIncorrectAnswers().get(2);

                correctAnswer=responses.get(0).getCorrectAnswer();

                String [] options= {wrongAns1, wrongAns2, wrongAns3, correctAns};

                //Initializing the RadioButtons
                radioButton = new RadioButton[NUMBER_OF_RADIOBUTTONS_TO_ADD];
                for (int i = 0; i < NUMBER_OF_RADIOBUTTONS_TO_ADD; i++) {
                    radioButton[i] = new RadioButton(QuestionActivity.this);

                    //Text can be loaded here
                    radioButton[i].setText(options[i]);

                }

                //Random Swapping
                for (int i = 0; i < 4; i++) {//this loop is randomly changing values 4 times
                    int swap_ind1 = ((int) (Math.random() * 10) % NUMBER_OF_RADIOBUTTONS_TO_ADD);
                    int swap_ind2 = ((int) (Math.random() * 10) % NUMBER_OF_RADIOBUTTONS_TO_ADD);
                    RadioButton temp = radioButton[swap_ind1];
                    radioButton[swap_ind1] = radioButton[swap_ind2];
                    radioButton[swap_ind2] = temp;
                }


                //Adding RadioButtons in RadioGroup
                for (int i = 0; i < NUMBER_OF_RADIOBUTTONS_TO_ADD; i++) {
                    radioGroup.addView(radioButton[i]);
                }




                textView.setText(responses.get(0).getQuestion());


                textView.setText(responses.get(0).getQuestion());


                questionID = responses.get(0).getId();
                Toast.makeText(QuestionActivity.this, "Correct ans "+correctAnswer, Toast.LENGTH_SHORT).show();


               // Toast.makeText(QuestionActivity.this, "Response successful for Question act", Toast.LENGTH_SHORT).show();
                loadingProgressBarDialog.dismissProgressBarDialog();


            }

            @Override
            public void onFailure(Call<ArrayList<Response>> call, Throwable t) {
                Toast.makeText(QuestionActivity.this, "Response failed for question Act", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkingAnswer() {
        mySharedPref = new MySharedPref(this);

        //condition for checking previous mark
        if (mySharedPref.getInt("passingValue") != null) {
            totalNumber = mySharedPref.getInt("passingValue");

        } else {
            totalNumber = 0;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {

                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(id);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Get the text of selected radiobutton through checkedRadioButton
                    radioButtonSelectedAnswer=checkedRadioButton.getText().toString();
                    if(radioButtonSelectedAnswer.equals(correctAnswer)){
                        totalNumber = totalNumber + 5;
                        mySharedPref.putInt("passingValue", totalNumber);

                    }
                }

            }
        });


        Log.e("Total number", " number " + totalNumber);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkClick = true;

                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                startActivity(intent);

                Toast.makeText(QuestionActivity.this, "Total number " + totalNumber, Toast.LENGTH_SHORT).show();

                finish();

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkClick) {

                    buttonNext.performClick();
                }
            }
        }, 8000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mySharedPref.clearData();
        startActivity(new Intent(QuestionActivity.this, MainActivity.class));

        //Code to exit app instantly
        /*moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }

}