package com.uchicago.yifan.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Yifan on 3/27/16.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDBHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITE = 102;
    static final int FAVORITE_WITH_ID = 103;

    private static final SQLiteQueryBuilder sMovieByIDQueryBuilder;

    private static final SQLiteQueryBuilder sFavoriteByIDQueryBuilder;

    static{
        sMovieByIDQueryBuilder = new SQLiteQueryBuilder();


        sMovieByIDQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME);

        sFavoriteByIDQueryBuilder = new SQLiteQueryBuilder();

        sFavoriteByIDQueryBuilder.setTables(
                MovieContract.FavoriteEntry.TABLE_NAME);
    }

    private static final String sMovieIDSelection = MovieContract.MovieEntry.TABLE_NAME +
                                                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sFavoriteIDSelection = MovieContract.FavoriteEntry.TABLE_NAME +
                                                    "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.MOVIE_PATH, MOVIE);
        matcher.addURI(authority, MovieContract.MOVIE_PATH + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.FAVORITE_PATH, FAVORITE);
        matcher.addURI(authority, MovieContract.FAVORITE_PATH + "/#", FAVORITE_WITH_ID);

        return matcher;
    }

    private Cursor getMovieWithID(Uri uri, String[] projection, String sortOrder){
        int movie_id = MovieContract.MovieEntry.getMovieIDFromUri(uri);

        return sMovieByIDQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                                            projection,
                                            sMovieIDSelection,
                                            new String[]{Integer.toString(movie_id)},
                                            null,
                                            null,
                                            sortOrder);
    }

    private Cursor getFavoriteWithID(Uri uri, String[] projection, String sortOrder){
        int movie_id = MovieContract.FavoriteEntry.getFavoriteIDFromUri(uri);

        return sFavoriteByIDQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                                            projection,
                                            sFavoriteIDSelection,
                                            new String[]{Integer.toString(movie_id)},
                                            null,
                                            null,
                                            sortOrder);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE:{
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MOVIE_WITH_ID:{
                retCursor = getMovieWithID(uri, projection, sortOrder);
                break;
            }
            case FAVORITE:{
                retCursor = mOpenHelper.getWritableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVORITE_WITH_ID:{
                retCursor = getFavoriteWithID(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into MOVIE TABLE: " + uri);
                }
                break;
            }
            case FAVORITE:{
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into FAVORITE TABLE: " + uri);
                }
                break;
            }
            default:
                throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:{
                db.beginTransaction();
                int retCount = 0;
                try {

                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            retCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            }
            case FAVORITE:{
                db.beginTransaction();
                int retCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            retCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri))
        {
            case MOVIE:{
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIE_WITH_ID:{
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});

                Log.d("DELETE: ", "movie " + String.valueOf(ContentUris.parseId(uri)) + " is deleted.");

                int fav = getContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null, null, null, null).getCount();

                Log.d("SUM: ", "There're " + fav + " movies.");

                break;
            }
            case FAVORITE:{
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID:{

                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ", new String[]{String.valueOf(ContentUris.parseId(uri))});

                Log.d("DELETE: ", "favorite " + String.valueOf(ContentUris.parseId(uri)) + " is deleted.");

                int fav = getContext().getContentResolver().query(
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        null, null, null, null).getCount();

                Log.d("SUM: ", "There're " + fav + " favorites.");

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
