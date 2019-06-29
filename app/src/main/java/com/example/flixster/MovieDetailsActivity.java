package com.example.flixster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.example.flixster.MovieListActivity.API_BASE_URL;
import static com.example.flixster.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends YouTubeBaseActivity {
    // the movie to display
    Movie movie;
    Config config;

    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivBackdrop;

    String movieID = "";

//    YouTubePlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ivBackdrop = (ImageView) findViewById(R.id.ivBackdrop);

//        player = (YouTubePlayerView) findViewById(R.id.ytPlayer);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        config = (Config) Parcels.unwrap(getIntent().getParcelableExtra(Config.class.getSimpleName()));

        final String imageURL = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        getTrailer();

        Glide.with(this)
                .load(imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.flicks_backdrop_placeholder))
                .into(ivBackdrop);

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        // set onClick for YT video
        ivBackdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                // create intent for the new activity
                Intent intent = new Intent(v.getContext(), MovieTrailerActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra("trailerID", movieID);
                // show the activity
                context.startActivity(intent);
            }
        });
    }

    private void getTrailer() {
        // create url
        String url = API_BASE_URL + "/movie/" + movie.getID() + "/videos";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.mdb_api_key)); // API key that is always required

        AsyncHttpClient client = new AsyncHttpClient();

        // execute GET request that expects a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");

                    if (results.length() == 0) { return; }

                    // get first object in the array and access the "key" object
                    movieID = results.getJSONObject(0).getString("key");

                } catch (JSONException e) {
                    Log.i("MovieDetailsActivity", "Failed to grab trailer id");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("MovieDetailsActivity", "Failed to get data from youtube trailer endpoint");
            }
        });
    }
}
