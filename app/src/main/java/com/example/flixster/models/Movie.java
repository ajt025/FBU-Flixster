package com.example.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Movie {

    // values
    public String title;
    public String overview;
    public String posterPath; // only path, not full URL
    public String backdropPath; // only path, not full URL
    public Double vote_average;

    public Movie() {}

    // initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        vote_average = object.getDouble("vote_average");
    }

    public Double getVoteAverage() {
        return vote_average;
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
