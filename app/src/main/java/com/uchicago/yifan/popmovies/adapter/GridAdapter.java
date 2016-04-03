package com.uchicago.yifan.popmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uchicago.yifan.popmovies.MoviesFragment;
import com.uchicago.yifan.popmovies.R;
import com.uchicago.yifan.popmovies.model.Movie;

import java.util.ArrayList;

/**
 * Created by Yifan on 2/24/16.
 */
public class GridAdapter extends CursorAdapter {

    private Context context;
    private ArrayList<Movie> movieList;

    public GridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String url = cursor.getString(MoviesFragment.COL_IMAGE);
        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_movie_imageview);
        Picasso.with(context).load(url).placeholder(R.mipmap.grid_placeholder).into(imageView);
    }

}
