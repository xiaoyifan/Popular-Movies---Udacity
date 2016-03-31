package com.uchicago.yifan.popmovies.queries;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.uchicago.yifan.popmovies.DetailActivityFragment;
import com.uchicago.yifan.popmovies.MoviesFragment;
import com.uchicago.yifan.popmovies.data.MovieContract;
import com.uchicago.yifan.popmovies.model.Movie;

import java.util.ArrayList;

/**
 * Created by Yifan on 3/31/16.
 */
public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

    private Context mContext;
    private MoviesFragment mFragment;

    public FetchFavoriteMoviesTask(Context context, MoviesFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                DetailActivityFragment.MOVIE_COLUMNS,
                null,
                null,
                null
        );

        ArrayList<Movie> movies = new ArrayList<>();
        cursor.moveToFirst();

        do {
            Movie movie = new Movie(cursor);
            movies.add(movie);
        } while (cursor.moveToNext());

        cursor.close();

        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        mFragment.setAdapter((ArrayList<Movie>) movies);
    }

}
