package com.uchicago.yifan.popmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.uchicago.yifan.popmovies.model.Movie;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null){
            Bundle arguments = new Bundle();

            Movie movie = getIntent().getParcelableExtra(DetailActivityFragment.DETAIL_URI);
            arguments.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getParcelableExtra(DetailActivityFragment.DETAIL_URI));

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

}
