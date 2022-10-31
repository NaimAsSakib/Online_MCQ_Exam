package com.example.onlinemcqexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionActivity extends AppCompatActivity {
    final int totalNumberOfRadioButton = 4;  //total number of RadioButtons
    private RadioButton[] radioButton;  //Array of radioButtons
    private RadioGroup radioGroup;

    private String correctAns, wrongAns1, wrongAns2, wrongAns3;
    private String radioButtonSelectedAnswer;

    private TextView tvQuestion;

    private AppCompatButton buttonNext, btnSubmit;

    private String baseURL = "https://the-trivia-api.com/api/";
    private ApiInterface apiInterface;
    private String selectedCategory;
    private int totalNumber;
    private MySharedPref mySharedPref;

    private LoadingProgressBarDialog loadingProgressBarDialog;
    private boolean checkClick;
    int countClick;
    int changeResponseIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mySharedPref = new MySharedPref(this);

        radioGroup = (RadioGroup) findViewById(R.id.radGroup);

        checkClick = false;

        loadingProgressBarDialog = new LoadingProgressBarDialog(this);

        tvQuestion = findViewById(R.id.tvQuestionAct);
        radioGroup = findViewById(R.id.radGroup);
        buttonNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);

        //getting selected category from MainActivity
        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("categoryName");

        //in if() condition is for calling the API only once for response value 0, save the response in a String in getData() which has 5 values/index/questions
        //in else condition convert the saved String response to Json object again and show the value from index 1 to 5 by calling the same activity again & again
        if (mySharedPref.getInt("countClickValueIndex") == 0) {
            networkLibraryInitializer();
            getData();
            changeResponseIndex = 1; //this variable is for changing response index everytime dynamically
            mySharedPref.putInt("countClickValueIndex", changeResponseIndex);

        } else {
            changeResponseIndex = mySharedPref.getInt("countClickValueIndex");
            // Toast.makeText(QuestionActivity.this, "response successful offline index value " + changeResponseIndex, Toast.LENGTH_SHORT).show();


            //converting string value saved in shared preference to Json object again
            ArrayList<Response> responsePojo = new Gson().fromJson(mySharedPref.getString("stringKey"), new TypeToken<List<Response>>() {
            }.getType());

            tvQuestion.setText(responsePojo.get(changeResponseIndex).getQuestion());  //setting question to textView

            //getting answers from API & keeping answers to String variables
            correctAns = responsePojo.get(changeResponseIndex).getCorrectAnswer();
            wrongAns1 = responsePojo.get(changeResponseIndex).getIncorrectAnswers().get(0);
            wrongAns2 = responsePojo.get(changeResponseIndex).getIncorrectAnswers().get(1);
            wrongAns3 = responsePojo.get(changeResponseIndex).getIncorrectAnswers().get(2);

           // Toast.makeText(QuestionActivity.this, "Correct ans " + correctAns, Toast.LENGTH_SHORT).show();


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

            changeResponseIndex = changeResponseIndex + 1;
            mySharedPref.putInt("countClickValueIndex", changeResponseIndex);

        }

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

        Call<ArrayList<Response>> responseQuestions = apiInterface.getQuestions(selectedCategory, 5);
        responseQuestions.enqueue(new Callback<ArrayList<Response>>() {
            @Override
            public void onResponse(Call<ArrayList<Response>> call, retrofit2.Response<ArrayList<Response>> response) {
                ArrayList<Response> responses = response.body();

                //converting response into string & saving that string to shared preference
                Gson gson = new Gson();
                String saveConvertedString = gson.toJson(response.body());
                mySharedPref.putString("stringKey", saveConvertedString);

                tvQuestion.setText(responses.get(changeResponseIndex - 1).getQuestion());  //setting question to textView


                //getting answers from API & keeping answers to String variables
                correctAns = responses.get(changeResponseIndex - 1).getCorrectAnswer(); //-1 is for matching the requirement
                wrongAns1 = responses.get(changeResponseIndex - 1).getIncorrectAnswers().get(0);
                wrongAns2 = responses.get(changeResponseIndex - 1).getIncorrectAnswers().get(1);
                wrongAns3 = responses.get(changeResponseIndex - 1).getIncorrectAnswers().get(2);

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


               // Toast.makeText(QuestionActivity.this, "Correct ans " + correctAns, Toast.LENGTH_SHORT).show();

                loadingProgressBarDialog.dismissProgressBarDialog();

            }

            @Override
            public void onFailure(Call<ArrayList<Response>> call, Throwable t) {
                Toast.makeText(QuestionActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
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
        if (mySharedPref.getInt("totalNumberForCorrectAns") != null) {
            totalNumber = mySharedPref.getInt("totalNumberForCorrectAns");

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
                        mySharedPref.putInt("totalNumberForCorrectAns", totalNumber);

                    }
                }

                Intent intent = new Intent(QuestionActivity.this, QuestionActivity.class);
                startActivity(intent);

               // Toast.makeText(QuestionActivity.this, "Total number " + totalNumber, Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        //countClick is for checking total next buttonClick
        countClick = mySharedPref.getInt("countButtonClick");  //initially for first question it will start from 1 before clicking next button
        countClick = countClick + 1;
        mySharedPref.putInt("countButtonClick", countClick);
        Log.e("clicked value", " value " + countClick);

        //condition for max number of question
        if (mySharedPref.getInt("countButtonClick") == 5) {  //5 is the max number of question
            buttonNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalNumber = totalNumber + 5;
                mySharedPref.putInt("totalNumberForCorrectAns", totalNumber);
                Log.e("total number ", "btnSubmit " + totalNumber);
                Intent intent1 = new Intent(QuestionActivity.this, ResultActivity.class);
                intent1.putExtra("totalQuestion", countClick);
                startActivity(intent1);
                finish();
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
    }

}