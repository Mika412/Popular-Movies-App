package com.example.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class MoviesProvider extends ContentProvider {
    String TAG = this.getClass().getSimpleName();


    static final int MOVIES = 100;
    static final int MOVIE_BY_ID = 101;
    static final int MOST_POPULAR_MOVIES = 201;
    static final int HIGHEST_RATED_MOVIES = 202;
    static final int FAVORITES = 300;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private static final String FAILED_TO_INSERT_ROW_INTO = "Failed to insert row into ";
    /*
    // movies._id = ?
    private static final String MOVIE_ID_SELECTION =
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID + " = ? ";
*/

    private MoviesDbHelper dbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;


        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_BY_ID);

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" +
                MoviesContract.PATH_MOST_POPULAR, MOST_POPULAR_MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" +
                MoviesContract.PATH_HIGHEST_RATED, HIGHEST_RATED_MOVIES);

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" +
                MoviesContract.PATH_FAVORITES, FAVORITES);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIES:
                cursor = dbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_BY_ID:
                cursor = getMovieById(uri, projection, sortOrder);
                break;
            case MOST_POPULAR_MOVIES:
                cursor = getMoviesFromReferenceTable(MoviesContract.MostPopularMovies.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case HIGHEST_RATED_MOVIES:
                cursor = getMoviesFromReferenceTable(MoviesContract.HighestRatedMovies.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case FAVORITES:
                cursor = getMoviesFromReferenceTable(MoviesContract.Favourites.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            default:
                return null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    private Cursor getMoviesFromReferenceTable(String tableName, String[] projection, String selection,
                                               String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        // tableName INNER JOIN movies ON tableName.movie_id = movies._id
        sqLiteQueryBuilder.setTables(
                tableName + " INNER JOIN " + MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + tableName + "." + MoviesContract.COLUMN_MOVIE_ID +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID
        );

        return sqLiteQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        long id = MoviesContract.getIdFromUri(uri);
        Log.e("asd", "Id i s+ " + id);
        String selection = MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID + " = ? ";
        String[] selectionArgs = new String[]{Long.toString(id)};
        return dbHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;
        long id;
        switch (match) {
            case MOVIES:
                id = db.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            case MOST_POPULAR_MOVIES:
                id = db.insert(MoviesContract.MostPopularMovies.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.MostPopularMovies.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            case HIGHEST_RATED_MOVIES:
                id = db.insert(MoviesContract.HighestRatedMovies.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.HighestRatedMovies.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            case FAVORITES:
                id = db.insert(MoviesContract.Favourites.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.Favourites.CONTENT_URI;
                } else {
                    throw new android.database.SQLException(FAILED_TO_INSERT_ROW_INTO + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated = 0;
        /*switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MostPopularMovies.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOST_POPULAR_MOVIES:
                rowsDeleted = db.delete(MoviesContract.MostPopularMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case HIGHEST_RATED_MOVIES:
                rowsDeleted = db.delete(MoviesContract.HighestRatedMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(MoviesContract.Favourites.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        int returnCount = 0;
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case MOST_POPULAR_MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        ContentValues newValue = new ContentValues();
                        newValue.put(MoviesContract.COLUMN_MOVIE_ID, value.getAsInteger(MoviesContract.MovieEntry._ID));
                        long id = db.insertWithOnConflict(MoviesContract.MostPopularMovies.TABLE_NAME, null, newValue, SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case HIGHEST_RATED_MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        ContentValues newValue = new ContentValues();
                        newValue.put(MoviesContract.COLUMN_MOVIE_ID, value.getAsInteger(MoviesContract.MovieEntry._ID));
                        long id = db.insertWithOnConflict(MoviesContract.HighestRatedMovies.TABLE_NAME, null, newValue, SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        return returnCount;
    }
}
