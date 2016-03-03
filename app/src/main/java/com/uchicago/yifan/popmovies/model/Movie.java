package com.uchicago.yifan.popmovies.model;

import java.io.Serializable;

/**
 * Created by Yifan on 2/24/16.
 */
public class Movie implements Serializable {

    private String originalTitle;
    private String overview;
    private double userRating;
    private String releaseDate;

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public Movie(String originalTitle, String overview, double userRating, String releaseDate, String imageUrl) {
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
    }

}
