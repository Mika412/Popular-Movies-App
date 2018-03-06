package com.example.android.popularmovies.Utilities;

import android.content.ContentValues;

import com.example.android.popularmovies.Data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains JSON based functions.
 */

public class JsonUtils {
    /** Converts a string with json data to json objects, and converts the data to an array of Movie objects. */
    public static ContentValues[] getMoviesContentValuesFromJson(String jsonString) throws JSONException {
        JSONObject jObject = new JSONObject(jsonString);
        JSONArray jArray = jObject.getJSONArray("results");
        ContentValues[] movieContentValues = new ContentValues[jArray.length()];
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObj = jArray.getJSONObject(i);
            String poster = jObj.getString("poster_path");
            String overview = jObj.getString("overview");
            String releaseDate = jObj.getString("release_date");
            int id = jObj.getInt("id");
            String title = jObj.getString("title");
            float rating = (float) jObj.getDouble("vote_average");

            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.MovieEntry._ID, id);
            movieValues.put(MoviesContract.COLUMN_TITLE, title);
            movieValues.put(MoviesContract.COLUMN_SYNOPSE, overview);
            movieValues.put(MoviesContract.COLUMN_POPULARITY, rating);
            movieValues.put(MoviesContract.COLUMN_POSTER_URL, poster);
            movieValues.put(MoviesContract.COLUMN_RELEASE_DATE, releaseDate);

            movieContentValues[i] = movieValues;

        }
        return movieContentValues;
    }
}
