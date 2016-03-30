package com.uchicago.yifan.popmovies;

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
import com.uchicago.yifan.popmovies.model.Movie;
import com.uchicago.yifan.popmovies.model.Review;
import com.uchicago.yifan.popmovies.model.Trailer;
import com.uchicago.yifan.popmovies.network.FetchReviewsTask;
import com.uchicago.yifan.popmovies.network.FetchTrailersTask;

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

    private Toast mToast;

    private Movie selectedMovie;

    private LinearListView mTrailersView;
    private LinearListView mReviewsView;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    public DetailActivityFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        // TODO Use fields...

        mTrailersView = (LinearListView) view.findViewById(R.id.detail_trailers);
        mReviewsView = (LinearListView) view.findViewById(R.id.detail_reviews);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            selectedMovie = (Movie) intent.getSerializableExtra(DetailActivity.EXTRA_MOVIE);
            if (selectedMovie != null) {

                loadContentsIntoDetailView(selectedMovie);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String id = Integer.toString(selectedMovie.getId());
        new FetchTrailersTask(this).execute(Integer.toString(selectedMovie.getId()));
        new FetchReviewsTask(this).execute(Integer.toString(selectedMovie.getId()));
    }

    @OnClick(R.id.movie_favorite_button)
    public void onFavored(ImageButton button) {
//        if (mMovie == null) return;
//
//        boolean favored = !selectedMovie.isFavored();
//        button.setSelected(favored);
//        mHelper.setMovieFavored(mMovie, favored);
        showToast(R.string.message_movie_favored);
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
