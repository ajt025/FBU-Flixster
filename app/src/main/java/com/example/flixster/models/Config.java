package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    // base url for loading images
    String imageBaseUrl;
    // poster size to use when fetching images, part of the url
    String posterSize;
    // backdrop size to use when fetching backdrop image, part of the url
    String backdropSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        // get the image base url
        imageBaseUrl = images.getString("secure_base_url");
        // get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        // use the option at index 3 or w342 as a fallback
        posterSize = posterSizeOptions.optString(3, "w342");
        // use the option at index 1 or w780 as a fallback
        backdropSize = posterSizeOptions.optString(5, "w780");
    }

    // helper method for creating urls
    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseUrl, size, path); // concatenate all 3
    }

    public String getImageBaseUrl() { return imageBaseUrl; }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() { return backdropSize; }
}
