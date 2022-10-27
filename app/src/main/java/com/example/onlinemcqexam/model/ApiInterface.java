package com.example.onlinemcqexam.model;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

  /*  @GET("questions")
    Call<ArrayList<Response>> getCategories(@Query("category") String categoryName);*/

    @GET("questions")
    Call<ArrayList<Response>> getCategories(@Query("categories") String categoryName);

    @GET("questions")
    Call<ArrayList<Response>> getQuestions(@Query("categories") String categoryName,
                                        @Query("limit") int limit );




}
