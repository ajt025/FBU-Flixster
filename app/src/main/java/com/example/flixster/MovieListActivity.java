package com.example.flixster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MovieListActivity extends AppCompatActivity {

    // constants
    // base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // the API key -- TODO move to a secure location
    public final static String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
