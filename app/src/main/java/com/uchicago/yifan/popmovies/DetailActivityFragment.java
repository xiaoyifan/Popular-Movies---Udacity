package com.uchicago.yifan.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uchicago.yifan.popmovies.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    String movie;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            Movie selectedMovie = (Movie) intent.getSerializableExtra(DetailActivity.EXTRA_MOVIE);
            if (selectedMovie != null) {

                loadContentsIntoDetailView(selectedMovie, rootView);
            }
        }

        return rootView;
    }

    private void loadContentsIntoDetailView(Movie selectedMovie, View rootView){

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title);
        titleView.setText(selectedMovie.getOriginalTitle());

        TextView releaseView = (TextView)rootView.findViewById(R.id.movie_release_date);
        releaseView.setText(selectedMovie.getReleaseDate());

        TextView overviewText = (TextView)rootView.findViewById(R.id.movie_overview);
        overviewText.setText(selectedMovie.getOverview());

        TextView ratingView = (TextView)rootView.findViewById(R.id.movie_rating);
        ratingView.setText("Rating: "+selectedMovie.getUserRating()+"/10");

        ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_poster);
        String url = selectedMovie.getImageUrl();
        Picasso.with(getContext()).load(url).placeholder(R.mipmap.grid_placeholder).into(imageView);

    }
}
