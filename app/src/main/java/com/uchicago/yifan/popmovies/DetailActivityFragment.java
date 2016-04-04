package com.uchicago.yifan.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;
import com.uchicago.yifan.popmovies.adapter.ReviewAdapter;
import com.uchicago.yifan.popmovies.adapter.TrailerAdapter;
import com.uchicago.yifan.popmovies.data.MovieContract;
import com.uchicago.yifan.popmovies.model.Movie;
import com.uchicago.yifan.popmovies.model.Review;
import com.uchicago.yifan.popmovies.model.Trailer;
import com.uchicago.yifan.popmovies.queries.FetchReviewsTask;
import com.uchicago.yifan.popmovies.queries.FetchTrailersTask;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Bind(R.id.movie_title) TextView titleView;
    @Bind(R.id.movie_release_date) TextView releaseView;
    @Bind(R.id.movie_overview) TextView overviewText;
    @Bind(R.id.movie_rating) TextView ratingView;
    @Bind(R.id.movie_poster) ImageView imageView;
    @Bind(R.id.detail_image) ImageView backdropView;
    @Bind(R.id.movie_favorite_button) ImageButton button;


    private Toast mToast;

    private Movie selectedMovie;

    private LinearListView mTrailersView;
    private LinearListView mReviewsView;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private boolean favored;

    public static final String DETAIL_URI = "detail_uri";

    public DetailActivityFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        // TODO Use fields...

        mTrailersView = (LinearListView) view.findViewById(R.id.detail_trailers);
        mReviewsView = (LinearListView) view.findViewById(R.id.detail_reviews);


        Bundle arguments = getArguments();
        if (arguments != null) {
            selectedMovie = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);

        }

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = getActivity().getIntent();

            if (selectedMovie != null) {

                loadContentsIntoDetailView(selectedMovie);
            }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (selectedMovie != null) {
            String id = Integer.toString(selectedMovie.getId());
            new FetchTrailersTask(this).execute(Integer.toString(selectedMovie.getId()));
            new FetchReviewsTask(this).execute(Integer.toString(selectedMovie.getId()));
        }
    }

    @OnClick(R.id.movie_favorite_button)
    public void onFavored(ImageButton button) {
        if (selectedMovie == null) return;

        favored = !favored;
//        boolean favored = !selectedMovie.isFavored();
        button.setSelected(favored);
        setMovieFavored(selectedMovie, favored);

        if (favored){
            showToast(R.string.message_movie_favored);
        }
        else {
            showToast(R.string.message_movie_unfavored);
        }

    }

    private void setMovieFavored(Movie movie, boolean favored){

        if (favored){
            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginalTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getUserRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.getImageUrl());
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE2, movie.getBackdropUrl());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            getContext().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, contentValues);
        }
        else {
            getContext().getContentResolver().delete(MovieContract.FavoriteEntry.buildFavoriteUri(movie.getId()), null, null);
        }
    }

    protected void showToast(@StringRes int resId) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void loadContentsIntoDetailView(Movie selectedMovie){

        titleView.setText(selectedMovie.getOriginalTitle());
        releaseView.setText(selectedMovie.getReleaseDate());
        overviewText.setText(selectedMovie.getOverview());
        ratingView.setText("Rating: "+selectedMovie.getUserRating()+"/10");

        int fav = getContext().getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(selectedMovie.getId()),
                null, null, null, null).getCount();

        favored = false;
        if (fav == 1){
                favored = true;
        }
        button.setSelected(favored);

        String url = selectedMovie.getImageUrl();
        String backdropUrl = selectedMovie.getBackdropUrl();
        Picasso.with(getContext()).load(url).placeholder(R.mipmap.grid_placeholder).into(imageView);
        Picasso.with(getContext()).load(backdropUrl).into(backdropView);

    }

    public void setTrailerAdapter(ArrayList<Trailer> trailerArrayList){

        LinearListView trailerListView = (LinearListView) getView().findViewById(R.id.detail_trailers);

        mTrailerAdapter = new TrailerAdapter(getActivity(), trailerArrayList);
        trailerListView.setAdapter(mTrailerAdapter);

        trailerListView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                Trailer trailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
            }

        });
    }

    public void setReviewsAdapter(ArrayList<Review> reviewArrayList){
        LinearListView reviewsListView = (LinearListView) getView().findViewById(R.id.detail_reviews);
        reviewsListView.setAdapter(new ReviewAdapter(getActivity(), reviewArrayList));

    }


}
