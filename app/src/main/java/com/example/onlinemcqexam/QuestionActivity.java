package com.example.onlinemcqexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinemcqexam.model.ApiInterface;
import com.example.onlinemcqexam.model.Response;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionActivity extends AppCompatActivity {

    TextView textView;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    AppCompatButton buttonNext;

    String baseURL = "https://the-trivia-api.com/api/";
    ApiInterface apiInterface;
    String selectedCategory;
    String questionID, correctAnswer;
    int totalNumber;
    MySharedPref mySharedPref;

    LoadingProgressBarDialog loadingProgressBarDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mySharedPref = new MySharedPref(this);

        loadingProgressBarDialog = new LoadingProgressBarDialog(this);


        textView = findViewById(R.id.tvQuestionAct);
        radioGroup = findViewById(R.id.radGroup);
        radioButton1 = findViewById(R.id.radBtnOption1);
        radioButton2 = findViewById(R.id.radBtnOption2);
        radioButton3 = findViewById(R.id.radBtnOption3);
        radioButton4 = findViewById(R.id.radBtnOption4);

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

                textView.setText(responses.get(0).getQuestion());
                radioButton1.setText(responses.get(0).getCorrectAnswer());
                radioButton2.setText(responses.get(0).getIncorrectAnswers().get(0));
                radioButton3.setText(responses.get(0).getIncorrectAnswers().get(1));
                radioButton4.setText(responses.get(0).getIncorrectAnswers().get(2));

                questionID = responses.get(0).getId();

                // Toast.makeText(QuestionActivity.this, "Response successful", Toast.LENGTH_SHORT).show();
                loadingProgressBarDialog.dismissProgressBarDialog();


            }

            @Override
            public void onFailure(Call<ArrayList<Response>> call, Throwable t) {
                Toast.makeText(QuestionActivity.this, "Response failed for question Act", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkingAnswer() {

        //condition for checking previous mark
        if (mySharedPref.getInt("passingValue") != null) {
            totalNumber = mySharedPref.getInt("passingValue");

        } else {
            totalNumber = 0;
        }


        //checking radiobutton id & setting condition

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();

                switch (id) {

                    case R.id.radBtnOption1:

                        totalNumber = totalNumber + 5;
                        mySharedPref.putInt("passingValue", totalNumber);

                    case R.id.radBtnOption2:
                        mySharedPref.putInt("passingValue", totalNumber);

                    case R.id.radBtnOption3:
                        mySharedPref.putInt("passingValue", totalNumber);

                    case R.id.radBtnOption4:
                        mySharedPref.putInt("passingValue", totalNumber);

                }
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionActivity.this, QuestionActivity.class);
                startActivity(intent);

                Toast.makeText(QuestionActivity.this, "Total number " + totalNumber, Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mySharedPref.clearData();
    }
}