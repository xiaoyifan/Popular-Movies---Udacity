package com.uchicago.yifan.popmovies.queries;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.uchicago.yifan.popmovies.MoviesFragment;
import com.uchicago.yifan.popmovies.data.MovieContract;
import com.uchicago.yifan.popmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yifan on 3/31/16.
 */
public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

    private Context mContext;
    private MoviesFragment mFragment;

    public FetchFavoriteMoviesTask(Context context, MoviesFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MoviesFragment.MOVIE_COLUMNS,
                null,
                null,
                null
        );

        List<Movie> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor);
                results.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return results;
    }

}
