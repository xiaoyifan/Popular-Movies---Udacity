package com.uchicago.yifan.popmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uchicago.yifan.popmovies.R;
import com.uchicago.yifan.popmovies.model.Movie;

import java.util.ArrayList;

/**
 * Created by Yifan on 2/24/16.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Movie> movieList;

    public GridAdapter(Context context, ArrayList<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Movie getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_movie, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);

        Movie movie = getItem(position);
        String url = movie.getImageUrl();
        Picasso.with(context).load(url).placeholder(R.mipmap.grid_placeholder).into(imageView);
        return imageView;
    }
}
