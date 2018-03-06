package com.example.android.popularmovies.Data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */
public final class MoviesContract {

    static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_MOVIES = "movies";
    static final String PATH_MOST_POPULAR = "most_popular";
    static final String PATH_HIGHEST_RATED = "highest_rated";
    static final String PATH_FAVORITES = "favorites";

    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SYNOPSE = "synopse";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_POSTER_URL = "poster_url";
    public static final String COLUMN_POPULARITY = "popularity";

    private MoviesContract() {
    }


    public static long getIdFromUri(Uri uri) {
        return ContentUris.parseId(uri);
    }
    /* Inner class that defines the contents of the movies table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class MostPopularMovies implements BaseColumns {
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_MOST_POPULAR)
                .build();
        public static final String TABLE_NAME = "most_popular_movies";

    }

    public static final class HighestRatedMovies implements BaseColumns {
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(PATH_HIGHEST_RATED)
                .build();
        public static final String TABLE_NAME = "highest_rated_movies";


    }

    public static final class Favourites implements BaseColumns {
        public static final Uri CONTENT_URI = MovieEntry.CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME = "favourites";

    }

}
