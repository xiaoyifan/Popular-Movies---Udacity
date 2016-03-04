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

                TextView titleView = (TextView) rootView.findViewById(R.id.movieTitle);
                titleView.setText(selectedMovie.getOriginalTitle());

                TextView releaseView = (TextView)rootView.findViewById(R.id.movieReleaseYear);
                releaseView.setText(selectedMovie.getReleaseDate());

                TextView overviewText = (TextView)rootView.findViewById(R.id.movieOverview);
                overviewText.setText(selectedMovie.getOverview());

                TextView ratingView = (TextView)rootView.findViewById(R.id.movieRating);
                ratingView.setText(selectedMovie.getUserRating()+"");

                ImageView imageView = (ImageView)rootView.findViewById(R.id.moviePoster);
               String url = selectedMovie.getImageUrl();
                Picasso.with(getContext()).load(url).into(imageView);
            }
        }

        return rootView;

    }
}
