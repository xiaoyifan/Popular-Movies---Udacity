package com.uchicago.yifan.popmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    private ArrayAdapter<String> mMovieAdapter;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mMovieAdapter = new ArrayAdapter<String>(
                //the current context, the fragment's parent activity
                getActivity(),
                //ID of list item layout
                R.layout.list_item_movie,
                //ID of the textview to populate
                R.id.list_item_movie_textview,
                //forecast data
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView myListView = (ListView) rootView.findViewById(R.id.listview_movie);

        myListView.setAdapter(mMovieAdapter);

        return rootView;
    }

    @Override
    public void onStart() {

        updateData();

        super.onStart();

    }

    public void updateData(){

        FetchMoviesTask moviesTask = new FetchMoviesTask();

        moviesTask.execute("popularity.desc");
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String[]>{

        final String LOG_TAG = MoviesFragment.class.getSimpleName();


        private String[] getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_TITLE = "original_title";
            final String OWM_AVG_VOTE = "vote_average";
            final String OWM_OVERVIEW = "overview";
            final String OWM_POSTERPATH = "poster_path";
            final String OWM_DATE = "release_date";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[movieArray.length()];


            for(int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject movieItem = movieArray.getJSONObject(i);

                String title = movieItem.getString(OWM_TITLE);
                String date = movieItem.getString(OWM_DATE);

                resultStrs[i] = title + " - " + date;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }


            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0)
                return null;


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try{

                String urlInit = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=27d49c583aca6b583253058da9cca0de";

                URL url = new URL(urlInit);

                Log.v(LOG_TAG, "THE URL IS: "+ urlInit);

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
                return getMovieDataFromJson(moviesJsonStr);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {
            super.onPostExecute(results);

            if(results != null){
                mMovieAdapter.clear();
                for (String dayForecast: results){
                    mMovieAdapter.add(dayForecast);
                }
            }

        }
    }
}
