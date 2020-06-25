package com.example.flixster;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    // list of movies
    ArrayList<Movie> movies;
    // config needed for image urls
    Config config;
    // context for rendering
    Context context;

    private final int radius = 20;

    // initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // get the context and create the inflater
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, viewGroup, false);
        // return a new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // get the movie data at the specified position
        Movie movie = movies.get(i);
        // populate the view with the movie data
        viewHolder.tvTitle.setText(movie.getTitle());
        viewHolder.tvOverview.setText('\t' + movie.getOverview()); // indent overview

        // determine orientation and respective image paths
        String imageUrl;
        int imagePlaceholder;
        ImageView imageView;

        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
            imagePlaceholder = R.drawable.flicks_movie_placeholder;
            imageView = viewHolder.ivPosterImage;
        } else { // must be landscape
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
            imagePlaceholder = R.drawable.flicks_backdrop_placeholder;
            imageView = viewHolder.ivBackdropImage;
        }

        // load image using Glide w/ transformation
        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                    .transform(new RoundedCorners(radius))
                    .placeholder(imagePlaceholder))
                .into(imageView);
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Movie movie = movies.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, MovieDetailsActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                        // also wrap the backdrop image for later
                        intent.putExtra(Config.class.getSimpleName(), Parcels.wrap(config));
                        // show the activity

                        context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                (Activity) context).toBundle());

                    }
                }
            });


            // handling text scrolling

            tvOverview.setMovementMethod(new ScrollingMovementMethod());
            View.OnTouchListener listener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean isLarger;
                    isLarger = ((TextView) v).getLineCount() * ((TextView) v).getLineHeight() > v.getHeight();
                    if (event.getAction() == MotionEvent.ACTION_MOVE && isLarger) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    return false;
                }
            };
            tvOverview.setOnTouchListener(listener);
        }
    }
}
