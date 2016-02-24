package com.uchicago.yifan.popmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
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


            return new String[0];
        }
    }
}
