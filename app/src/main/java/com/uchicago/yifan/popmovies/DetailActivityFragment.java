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

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Bind(R.id.movie_title) TextView titleView;
    @Bind(R.id.movie_release_date) TextView releaseView;
    @Bind(R.id.movie_overview) TextView overviewText;
    @Bind(R.id.movie_rating) TextView ratingView;
    @Bind(R.id.movie_poster) ImageView imageView;
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

        titleView.setText(selectedMovie.getOriginalTitle());
        releaseView.setText(selectedMovie.getReleaseDate());
        overviewText.setText(selectedMovie.getOverview());
        ratingView.setText("Rating: "+selectedMovie.getUserRating()+"/10");

        String url = selectedMovie.getImageUrl();
        Picasso.with(getContext()).load(url).placeholder(R.mipmap.grid_placeholder).into(imageView);

    }
}
