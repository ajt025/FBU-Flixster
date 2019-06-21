package com.example.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {

    // values
    private String title;
    private String overview;
    private String posterPath; // only path, not full URL
    private String backdropPath; // only path, not full URL

    // initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() { return backdropPath; }
}
