package com.uchicago.yifan.popmovies.model;

import android.database.Cursor;

import com.uchicago.yifan.popmovies.DetailActivityFragment;

import java.io.Serializable;

/**
 * Created by Yifan on 2/24/16.
 */
public class Movie implements Serializable {

    private int id;
    private String originalTitle;
    private String overview;
    private double userRating;
    private String releaseDate;

    private String imageUrl;
    private String backdropUrl;

    public String getOriginalTitle(){
        return originalTitle;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public String getBackdropUrl(){
        return backdropUrl;
    }

    public String getOverview(){
        return overview;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public double getUserRating(){
        return userRating;
    }

    public int getId(){
        return id;
    }

    public Movie(int id, String originalTitle, String overview, double userRating, String releaseDate, String imageUrl, String backdropUrl) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
        this.backdropUrl = backdropUrl;
    }

    public Movie(Cursor cursor){
        this.id = cursor.getInt(DetailActivityFragment.COL_MOVIE_ID);
        this.originalTitle = cursor.getString(DetailActivityFragment.COL_TITLE);
        this.overview = cursor.getString(DetailActivityFragment.COL_OVERVIEW);
        this.userRating = cursor.getDouble(DetailActivityFragment.COL_RATING);
        this.releaseDate = cursor.getString(DetailActivityFragment.COL_DATE);
        this.imageUrl = cursor.getString(DetailActivityFragment.COL_IMAGE);
        this.backdropUrl = cursor.getString(DetailActivityFragment.COL_IMAGE2);
    }

}
