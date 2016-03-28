package com.uchicago.yifan.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uchicago.yifan.popmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Yifan on 3/27/16.
 */
//Cite: the Async Task code in our Sunshine app in the tutorial: https://github.com/udacity/Sunshine-Version-2
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private final Context mContext;


    public FetchMoviesTask(Context context){
        mContext = context;
    }

    public void createMovieListFromJson(String moviesJsonString) throws JSONException {

        JSONObject moviesJson = new JSONObject(moviesJsonString);

        final String MOVIEDB_RESULTS = "results";
        JSONArray moviesArray = moviesJson.getJSONArray(MOVIEDB_RESULTS);

        final String movie_id = "id";
        final String movie_original_title = "original_title";
        final String movie_overview = "overview";
        final String movie_vote_avg = "vote_average";
        final String movie_release_date = "release_date";
        final String movie_poster_path = "poster_path";
        final String movie_backdrop_path = "backdrop_path";
        final String poster_URL = "http://image.tmdb.org/t/p/";
        String posterSize = "w185";
        String backdropSize = "w342";

    try {


        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());


        for (int i = 0; i < moviesArray.length(); ++i) {

            JSONObject movieJsonObject = moviesArray.getJSONObject(i);

            String originalTitle = movieJsonObject.getString(movie_original_title);

            int id = movieJsonObject.getInt(movie_id);
            String overview = movieJsonObject.getString(movie_overview);
            double voteAvg = movieJsonObject.getDouble(movie_vote_avg);
            String releaseDate = movieJsonObject.getString(movie_release_date);

            String posterPath = movieJsonObject.getString(movie_poster_path);
            String backdropPath = movieJsonObject.getString(movie_backdrop_path);

            Uri.Builder posterUrl = Uri.parse(poster_URL).buildUpon();
            Uri.Builder backdropUrl = Uri.parse(poster_URL).buildUpon();

            posterUrl.appendEncodedPath(posterSize);
            posterUrl.appendEncodedPath(posterPath);

            backdropUrl.appendEncodedPath(backdropSize);
            backdropUrl.appendEncodedPath(backdropPath);

            String imageUrl = posterUrl.toString();
            String backUrl = backdropUrl.toString();

            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, originalTitle);
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, voteAvg);
            contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, releaseDate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, imageUrl);
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE2, backUrl);

            cVVector.add(contentValues);
        }

            int inserted = 0;

            if  (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

        Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");
        }
        catch (JSONException e){

            e.printStackTrace();
        }


    }



    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0)
            return null;


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try{

            final String baseUrl = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY = "api_key";

            Uri queryUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(API_KEY, Constants.API_KEY)
                    .build();

            URL url = new URL(queryUri.toString());

            Log.v(LOG_TAG, "THE URL IS: " + url);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
            }
            moviesJsonStr = buffer.toString();
            createMovieListFromJson(moviesJsonStr);
            Log.v("Result", moviesJsonStr);

        }catch (IOException e) {
            Log.e("MovieFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MovieFragment", "Error closing stream", e);
                }
            }
        }


        return null;
    }

}