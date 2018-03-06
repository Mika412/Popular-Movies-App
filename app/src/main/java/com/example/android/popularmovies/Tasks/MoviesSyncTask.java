package com.example.android.popularmovies.Tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies.Data.MoviesContract;
import com.example.android.popularmovies.Utilities.Constants;
import com.example.android.popularmovies.Utilities.JsonUtils;
import com.example.android.popularmovies.Utilities.NetworkUtils;

import java.net.URL;

public class MoviesSyncTask {
    synchronized public static void syncData(Context context) {
        try {
            URL url = NetworkUtils.buildURL(Constants.SORT_POPULAR);
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            ContentValues[] movies = JsonUtils.getMoviesContentValuesFromJson(response);
            Uri insertContentUri = MoviesContract.MostPopularMovies.CONTENT_URI;

            if (movies != null && movies.length != 0) {
                ContentResolver moviesContentResolver = context.getContentResolver();
                moviesContentResolver.delete(
                        insertContentUri,
                        null,
                        null);
                moviesContentResolver.bulkInsert(
                        insertContentUri,
                        movies);
                moviesContentResolver.bulkInsert(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        movies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            URL url = NetworkUtils.buildURL(Constants.SORT_TOP_RATED);
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            ContentValues[] movies = JsonUtils.getMoviesContentValuesFromJson(response);
            Uri insertContentUri = MoviesContract.HighestRatedMovies.CONTENT_URI;

            if (movies != null && movies.length != 0) {
                ContentResolver moviesContentResolver = context.getContentResolver();
                moviesContentResolver.delete(
                        insertContentUri,
                        null,
                        null);
                moviesContentResolver.bulkInsert(
                        insertContentUri,
                        movies);
                moviesContentResolver.bulkInsert(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        movies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}