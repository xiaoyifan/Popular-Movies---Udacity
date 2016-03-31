package com.uchicago.yifan.popmovies.queries;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uchicago.yifan.popmovies.Constants;
import com.uchicago.yifan.popmovies.DetailActivityFragment;
import com.uchicago.yifan.popmovies.model.Review;

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
import java.util.List;

/**
 * Created by Yifan on 3/29/16.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    DetailActivityFragment detail_fragment;


    public FetchReviewsTask(DetailActivityFragment fragment){
        detail_fragment = fragment;
    }

    private List<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
        JSONObject reviewJson = new JSONObject(jsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray("results");

        List<Review> results = new ArrayList<>();

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            results.add(new Review(review));
        }

        return results;
    }

    @Override
    protected List<Review> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
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
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getReviewsDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {

        detail_fragment.setReviewsAdapter((ArrayList<Review>) reviews);
    }
}
