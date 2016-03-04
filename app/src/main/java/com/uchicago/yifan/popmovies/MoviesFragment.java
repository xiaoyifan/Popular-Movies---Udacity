package com.uchicago.yifan.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.uchicago.yifan.popmovies.adapter.GridAdapter;
import com.uchicago.yifan.popmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    @Override
    public void onStart() {

        updateData();

        super.onStart();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateData(){

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String value = preferences.getString("sortby", "popularity.desc");
        moviesTask.execute(value);
    }

    private void setAdapter( final ArrayList<Movie> movieList )
    {
        GridView gridview = (GridView) getView().findViewById(R.id.gridview);
        gridview.setAdapter(new GridAdapter(getActivity(), movieList));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = movieList.get(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(DetailActivity.EXTRA_MOVIE, selectedMovie);
                startActivity(detailIntent);
            }
        });

    }

    //Cite: the Async Task code in our Sunshine app in the tutorial: https://github.com/udacity/Sunshine-Version-2
    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        final String LOG_TAG = MoviesFragment.class.getSimpleName();


        public ArrayList<Movie> createMovieListFromJson(String moviesJsonString) throws JSONException {

            JSONObject moviesJson = new JSONObject(moviesJsonString);

            final String MOVIEDB_RESULTS = "results";
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIEDB_RESULTS);

            ArrayList<Movie> movieList = new ArrayList<>();
            for(int movieNumber = 0; movieNumber < moviesArray.length(); ++movieNumber)
            {
                Movie movie = parseJsonMovieObject(moviesArray.getJSONObject(movieNumber));
                movieList.add(movie);
            }


            return movieList;
        }



        private Movie parseJsonMovieObject(JSONObject movieJsonObject) throws JSONException {

            final String movie_original_title = "original_title";
            String originalTitle = movieJsonObject.getString(movie_original_title);

            final String movie_overview = "overview";
            String overview = movieJsonObject.getString(movie_overview);

            final String movie_vote_avg = "vote_average";
            double voteAvg = movieJsonObject.getDouble(movie_vote_avg);

            final String movie_release_date = "release_date";
            String releaseDate = movieJsonObject.getString(movie_release_date);

            final String movie_poster_path = "poster_path";
            String posterPath = movieJsonObject.getString(movie_poster_path);

            final String poster_URL = "http://image.tmdb.org/t/p/";
            Uri.Builder posterUrl = Uri.parse(poster_URL).buildUpon();
            String posterSize = "w185";
            posterUrl.appendEncodedPath( posterSize );
            posterUrl.appendEncodedPath(posterPath);

            String imageUrl = posterUrl.toString();

            return new Movie(originalTitle, overview, voteAvg, releaseDate, imageUrl);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

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

                Log.v(LOG_TAG, "THE URL IS: "+ url);

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

                Log.v("Result", moviesJsonStr);

            }catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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


            try{
                return createMovieListFromJson(moviesJsonStr);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieList) {

            MoviesFragment.this.setAdapter(movieList);

        }
    }
}
