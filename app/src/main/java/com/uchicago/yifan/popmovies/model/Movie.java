//Cite: https://github.com/ismaeltoe/Popular-Movies-App/blob/master/app/src/main/java/com/github/ismaeltoe/movies/model/Movie.java
package com.uchicago.yifan.popmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.uchicago.yifan.popmovies.MoviesFragment;

/**
 * Created by Yifan on 2/24/16.
 */
public class Movie implements Parcelable {

    private int id;
    private String originalTitle;
    private String overview;
    private String userRating;
    private String releaseDate;
    private String popularity;

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

    public String getUserRating(){
        return userRating;
    }

    public String getPopularity(){
        return popularity;
    }

    public int getId(){
        return id;
    }

    public Movie(int id, String originalTitle, String overview, String userRating, String releaseDate, String popularity, String imageUrl, String backdropUrl) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.imageUrl = imageUrl;
        this.backdropUrl = backdropUrl;
        this.popularity = popularity;
    }

    public Movie(Cursor cursor){

        String id = cursor.getString(MoviesFragment.COL_MOVIE_ID);

        this.id = cursor.getInt(MoviesFragment.COL_MOVIE_ID);
        this.originalTitle = cursor.getString(MoviesFragment.COL_TITLE);
        this.overview = cursor.getString(MoviesFragment.COL_OVERVIEW);
        this.userRating = cursor.getString(MoviesFragment.COL_RATING);
        this.releaseDate = cursor.getString(MoviesFragment.COL_DATE);
        this.imageUrl = cursor.getString(MoviesFragment.COL_IMAGE);
        this.backdropUrl = cursor.getString(MoviesFragment.COL_IMAGE2);
        this.popularity = cursor.getString(MoviesFragment.COL_POPULARITY);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(imageUrl);
        dest.writeString(backdropUrl);
        dest.writeString(overview);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeString(popularity);
    }

    private Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        imageUrl = in.readString();
        backdropUrl = in.readString();
        overview = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        popularity = in.readString();
    }
}
