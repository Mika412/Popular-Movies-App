package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.PosterAdapter;
import com.example.android.popularmovies.Adapters.PosterAdapter.ListItemClickListener;
import com.example.android.popularmovies.Data.MoviesContract;
import com.example.android.popularmovies.Network.MoviesSyncUtils;
import com.example.android.popularmovies.Utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private String activityName = this.getClass().getSimpleName();


    @BindView(R.id.pb_loading) ProgressBar progressBar;
    @BindView(R.id.tv_error) TextView errorMessageTV;
    @BindView(R.id.no_movies) TextView noMoviesTV;
    @BindView(R.id.rv_posters) RecyclerView recyclerView;
    private PosterAdapter posterAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    String sort_by = "popular";

    private static final int ID_POSTER_LOADER = 44;

    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_SYNOPSE= 2;
    public static final int INDEX_RELEASE_DATE = 3;
    public static final int INDEX_POSTER_URL = 4;
    public static final int INDEX_POPULARITY = 5;



    public static final String[] MAIN_FORECAST_PROJECTION = {
            MoviesContract.COLUMN_MOVIE_ID,
            MoviesContract.COLUMN_TITLE,
            MoviesContract.COLUMN_SYNOPSE,
            MoviesContract.COLUMN_RELEASE_DATE,
            MoviesContract.COLUMN_POSTER_URL,
            MoviesContract.COLUMN_POPULARITY,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferencesEditor = sharedPreferences.edit();

        sort_by = sharedPreferences.getString(Constants.MODE_VIEW, Constants.SORT_POPULAR);

        posterAdapter = new PosterAdapter(this);
        recyclerView.setAdapter(posterAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.movies_columns));
        recyclerView.setLayoutManager(layoutManager);


        getLoaderManager().initLoader(ID_POSTER_LOADER, null, this);
        MoviesSyncUtils.startImmediateSync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri insertContentUri;
        if(sort_by.equals(Constants.SORT_POPULAR))
            insertContentUri = MoviesContract.MostPopularMovies.CONTENT_URI;
        else if(sort_by.equals(Constants.SORT_TOP_RATED))
            insertContentUri = MoviesContract.HighestRatedMovies.CONTENT_URI;
        else
            insertContentUri = MoviesContract.Favourites.CONTENT_URI;
        Log.e("test", "Rotations" + sort_by);
        Log.e("Ds", "dsa" + posterAdapter.getItemCount());
        return new CursorLoader(this,
                insertContentUri,
                MAIN_FORECAST_PROJECTION,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        posterAdapter.swapCursor(data);
        if (data != null && data.getCount() != 0) showPosters();
        else showNone();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    public void onClick(int id) {

        Intent movieDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri movieDetailUri = MoviesContract.MovieEntry.buildMovieUri(id);
        movieDetailIntent.setData(movieDetailUri);
        startActivity(movieDetailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if(sort_by.equals(Constants.SORT_TOP_RATED)){
            menu.findItem(R.id.sort_by_top_rated).setChecked(true);
        }else if(sort_by.equals(Constants.SORT_POPULAR)){
            menu.findItem(R.id.sort_by_popular).setChecked(true);
        }else{
            menu.findItem(R.id.sort_by_favourite).setChecked(true);
        }
        return true;
    }

    /** Sets recycleview as invisible, textview and progress bar as visible. */

    public void showErrorMessage(){
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTV.setVisibility(View.VISIBLE);
    }

    /** Sets recycleview as visible, that contains the movie posters, textview and progress bar as invisible. */
    public void showPosters(){
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        errorMessageTV.setVisibility(View.INVISIBLE);
        noMoviesTV.setVisibility(View.INVISIBLE);
    }
    /** Sets recycleview as visible, that contains the movie posters, textview and progress bar as invisible. */
    public void showNone(){
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        noMoviesTV.setVisibility(View.VISIBLE);
        errorMessageTV.setVisibility(View.INVISIBLE);
    }
    /** Sets recycleview as visible, that contains the movie posters, textview and progress bar as invisible. */
    public void hidePosters(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTV.setVisibility(View.INVISIBLE);
        noMoviesTV.setVisibility(View.INVISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_by_popular:
                sort_by = Constants.SORT_POPULAR;
                sharedPreferencesEditor.putString(Constants.MODE_VIEW, sort_by);
                sharedPreferencesEditor.apply();
                restartLoader();
                item.setChecked(true);
                break;

            case R.id.sort_by_top_rated:
                sort_by = Constants.SORT_TOP_RATED;
                sharedPreferencesEditor.putString(Constants.MODE_VIEW, sort_by);
                sharedPreferencesEditor.apply();
                restartLoader();
                item.setChecked(true);
                break;
            case R.id.sort_by_favourite:
                sort_by = Constants.SORT_FAVOURITES;
                sharedPreferencesEditor.putString(Constants.MODE_VIEW, sort_by);
                sharedPreferencesEditor.apply();
                restartLoader();
                item.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restartLoader() {
        posterAdapter.swapCursor(null);
        getLoaderManager().restartLoader(ID_POSTER_LOADER, null, this);
    }
}
