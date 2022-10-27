package com.example.onlinemcqexam;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPref {     //This class is taken only for shared preferences purpose
    // through this class I can us it's methods by making its object anywhere in the project
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MySharedPref(Context context) {
        sharedPreferences=context.getSharedPreferences("myPreferences",context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public void putInt(String key, int value){
        editor.putInt(key,value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value){
        editor.putBoolean(key,value);
        editor.apply();
    }
    public void putString(String key, String value){
        editor.putString(key,value);
        editor.apply();
    }
    public void putFloat(String key, float value){
        editor.putFloat(key,value);
        editor.apply();
    }
    public void putDouble(String key, double value){
        editor.putFloat(key, (float) value);
        editor.apply();
    }
    public void putLong(String key, Long value){
        editor.putLong(key,value);
        editor.apply();
    }


    public Integer getInt(String key){
        return sharedPreferences.getInt(key,0); //setting default value
    }
    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }
    public float getDouble(String key){
        return sharedPreferences.getFloat(key,0);
    }

    public Long getLong(String key){
        return sharedPreferences.getLong(key,0);
    }

    public void clearData(){
        editor.clear();
        editor.apply();
    }

}
