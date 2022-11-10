package com.example.onlinemcqexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.onlinemcqexam.model.ApiInterface;
import com.example.onlinemcqexam.model.ResponseCategory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    MySharedPref mySharedPref;
    LoadingProgressBarDialog loadingProgressBarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingProgressBarDialog=new LoadingProgressBarDialog(this);

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

        //API calling
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
        loadingProgressBarDialog.startProgressBarLoading();
        mySharedPref.clearData();  // clearing previous data from shared pref

        Call<ResponseBody> responseCategoryDetails = apiInterface.getCategories();
        responseCategoryDetails.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // See Json & pojo 'ResponseCategory' data first, Json is object type but I can't get the keys in adapter. It's backend fault. Bad but rare type Api.
                // I need the keys in rcv adapter, not the List<> type values within the keys. So, General API call methods won't work.

                //Means I can't use Pojo for getting data as pojo/json is faulty. Rather I have to convert full response body into String.
                //Then I have to keep that value in hashmap within an arrayList as in Jason values are in List<>, I will keep keys in hashmap keys.
                // Then hashmap a for loop chaliye key gulo niye adapter a set korbo. Let's see step by step

                try {
                    String responseCategories = response.body().string(); //converting response body into String

                    //Next three lines are for keeping that string into hashmap, defining hashmap type,
                    // converting the string into response again within hashmap to get all data within hashmap
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, ArrayList<String>>>() {
                    }.getType();
                    Map<String, ArrayList<String>> myMap = gson.fromJson(responseCategories, type);

                    ArrayList<String> arrayList = new ArrayList<>(); //taking an empty arraylist

                    //this is the way of using for loop for getting key/value from hashmap, as general for loop doesn't work for hashmap
                    for (Map.Entry<String, ArrayList<String>> mapElement : myMap.entrySet()) {
                        arrayList.add(mapElement.getKey());  //keeping hashmap keys in arrayList as I need those keys
                    }
                    //setting that arrayList in adapter
                    programAdapter = new MainActAdapter(MainActivity.this, arrayList);
                    recyclerView.setAdapter(programAdapter);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                loadingProgressBarDialog.dismissProgressBarDialog();
                //Toast.makeText(MainActivity.this, "Response successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingProgressBarDialog.dismissProgressBarDialog();
                Toast.makeText(MainActivity.this, "Please Check your internet connection", Toast.LENGTH_SHORT).show();

            }
        });

    }
}