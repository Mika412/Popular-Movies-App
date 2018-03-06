package com.example.android.popularmovies.Network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.android.popularmovies.Data.MoviesContract;

public class FavoritesService {

    private final Context context;

    public FavoritesService(Context context) {
        this.context = context.getApplicationContext();
    }

    public void addToFavorites(long movieId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID, movieId);
        context.getContentResolver().insert(MoviesContract.Favourites.CONTENT_URI, contentValues);
    }

    public void removeFromFavorites(long movieId) {
        context.getContentResolver().delete(
                MoviesContract.Favourites.CONTENT_URI,
                MoviesContract.COLUMN_MOVIE_ID + " = " + movieId,
                null
        );
    }

    public boolean isFavorite(long movieId) {
        boolean favorite = false;
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.Favourites.CONTENT_URI,
                null,
                MoviesContract.COLUMN_MOVIE_ID + " = " + movieId,
                null,
                null
        );
        if (cursor != null) {
            favorite = cursor.getCount() != 0;
            cursor.close();
        }
        return favorite;
    }
}
