package com.example.onlinemcqexam;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    MySharedPref mySharedPref;
    TextView tvMarksObtained, tvTotalMarks, tvTotalQuestion, tvTotalCorrectAns, tvTotalIncorrectAnswer;
    int totalCorrectAnswer, totalIncorrectAnswer, totalMarksObtained, totalQuestionAsked, numberPerQuestion, fullMarks;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mySharedPref=new MySharedPref(this);

        tvMarksObtained=findViewById(R.id.totalMarksObtainedNumber);
        tvTotalMarks=findViewById(R.id.totalMarksNumber);
        tvTotalQuestion=findViewById(R.id.totalQuestionNumber);
        tvTotalCorrectAns=findViewById(R.id.totalCorrectAnsNumber);
        tvTotalIncorrectAnswer=findViewById(R.id.totalIncorrectAnsNumber);

        numberPerQuestion=5;
        totalMarksObtained= mySharedPref.getInt("totalNumberForCorrectAns")-5;
        totalQuestionAsked=mySharedPref.getInt("countButtonClick");
        fullMarks=totalQuestionAsked*numberPerQuestion;

        Log.e("total marks"," "+totalMarksObtained);
        Log.e("total question asked"," "+totalQuestionAsked);
        Log.e("total full marks"," "+fullMarks);

        if(totalMarksObtained>0){
            totalCorrectAnswer=totalMarksObtained/numberPerQuestion;
        }
        totalIncorrectAnswer=totalQuestionAsked-totalCorrectAnswer;

        Toast.makeText(ResultActivity.this,"total correct num "+totalMarksObtained,Toast.LENGTH_SHORT).show();

        tvMarksObtained.setText(Integer.toString(totalMarksObtained));
        tvTotalMarks.setText(Integer.toString(fullMarks));
        tvTotalQuestion.setText(Integer.toString(totalQuestionAsked));
        tvTotalCorrectAns.setText(totalCorrectAnswer+"");
        tvTotalIncorrectAnswer.setText(totalIncorrectAnswer+"");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mySharedPref.clearData();
        Intent intent=new Intent(ResultActivity.this,MainActivity.class);
        startActivity(intent);
    }
}