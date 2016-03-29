package com.uchicago.yifan.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.uchicago.yifan.popmovies.network.FetchMoviesTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private GridAdapter adapter;

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

        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String value = preferences.getString("sortby", "popularity.desc");
        moviesTask.execute(value);
    }

    public void setAdapter( final ArrayList<Movie> movieList )
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

}
