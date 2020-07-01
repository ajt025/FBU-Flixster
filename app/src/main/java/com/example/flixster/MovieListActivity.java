package com.example.flixster;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.flixster.databinding.ActivityMovieListBinding;
import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    // constants
    private ActivityMovieListBinding binding; // view binding instance
    // base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging
    public final static String TAG = "MovieListActivity";
    // list of currently playing movies
    ArrayList<Movie> movies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;
    // image config
    Config config;

    // instance fields
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Slide(Gravity.LEFT));
        binding = ActivityMovieListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init. client
        client = new AsyncHttpClient();
        // init. list of movies
        movies = new ArrayList<>();
        // initialize the adapter -- movies array cannot be reinitialized after this pt
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and the adapter
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        binding.rvMovies.setLayoutManager(mLayoutManager);
        binding.rvMovies.setAdapter(adapter);

        // add dividers to movie list
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(binding.rvMovies.getContext(),
                mLayoutManager.getOrientation());
        binding.rvMovies.addItemDecoration(mDividerItemDecoration);

        // get config. on app creation
        getConfiguration();

    }

    public static void print() {
        System.out.println("wee");
    }

    // get the list of currently playing movies from the API
    private void getNowPlaying() {
        // create url
        String url = API_BASE_URL + "/movie/now_playing";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.mdb_api_key)); // API key that is always required

        // execute GET request that expects a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into movie list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through result set and create Movie objects
                    for (int i = 0; i < results.length(); ++i) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now playing endpoint", throwable, true);
            }
        });
    }

    // get configuration from API
    private void getConfiguration() {
        // create url
        String url = API_BASE_URL + "/configuration";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.mdb_api_key)); // API key that is always required

        // execute GET request that expects a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s",
                            config.getImageBaseUrl(),
                            config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);
                    // get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast w/ error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
