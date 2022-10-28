package com.example.onlinemcqexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.onlinemcqexam.model.ApiInterface;
import com.example.onlinemcqexam.model.Response;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter programAdapter;
    RecyclerView.LayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;

    // String[] categoryName = {"Sports", "Music", "Politics", "Dance", "Geography"};

    String baseURL = "https://the-trivia-api.com/api/";
    ApiInterface apiInterface;
    HashMap<String, String> hashMap;
    MySharedPref mySharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySharedPref = new MySharedPref(this);
        mySharedPref.clearData();

        recyclerView = findViewById(R.id.rcvMainAct);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        //for showing recyclerview as gridView
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        /*programAdapter = new MainActAdapter(this, categoryName);
        recyclerView.setAdapter(programAdapter);*/
        networkLibraryInitializer();
        getData();

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
        mySharedPref.clearData();

        Call<ArrayList<Response>> responseDetails = apiInterface.getCategories("");
        responseDetails.enqueue(new Callback<ArrayList<Response>>() {
            @Override
            public void onResponse(Call<ArrayList<Response>> call, retrofit2.Response<ArrayList<Response>> response) {
                ArrayList<Response> responses = response.body();

                programAdapter = new MainActAdapter(MainActivity.this, responses);
                recyclerView.setAdapter(programAdapter);
                Toast.makeText(MainActivity.this, "Response successful", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ArrayList<Response>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}