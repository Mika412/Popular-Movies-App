package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.Data.MoviesContract.*;
/**
 * Manages a local database for movies.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_SCHEMA_VERSION = 2;
    private static final String SQL_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                        MoviesContract.COLUMN_TITLE + " TEXT, " +
                        MoviesContract.COLUMN_SYNOPSE + " TEXT, " +
                        MoviesContract.COLUMN_RELEASE_DATE + " TEXT, " +
                        MoviesContract.COLUMN_POSTER_URL + " TEXT, " +
                        MoviesContract.COLUMN_POPULARITY + " REAL" +
                        " );";
        final String SQL_CREATE_MOST_TABLE =
                "CREATE TABLE " + MostPopularMovies.TABLE_NAME + " (" +
                        MostPopularMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + MoviesContract.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +

                        " );";
        final String SQL_CREATE_HIGHEST_TABLE =
                "CREATE TABLE " + HighestRatedMovies.TABLE_NAME + " (" +
                        HighestRatedMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + MoviesContract.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +

                        " );";

        final String SQL_CREATE_FAVOURITE_TABLE =
                "CREATE TABLE " + Favourites.TABLE_NAME + " (" +
                        HighestRatedMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                        " FOREIGN KEY (" + MoviesContract.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +

                        " );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_MOST_TABLE);
        db.execSQL(SQL_CREATE_HIGHEST_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.MovieEntry.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.MostPopularMovies.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.HighestRatedMovies.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.Favourites.TABLE_NAME);
        onCreate(db);
    }
}
