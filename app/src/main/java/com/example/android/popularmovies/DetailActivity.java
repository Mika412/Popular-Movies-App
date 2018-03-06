package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.ReviewListAdapter;
import com.example.android.popularmovies.Adapters.TrailerListAdapter;
import com.example.android.popularmovies.Data.MoviesContract;
import com.example.android.popularmovies.Models.Review;
import com.example.android.popularmovies.Models.Trailer;
import com.example.android.popularmovies.Network.FavoritesService;
import com.example.android.popularmovies.Tasks.FetchReviewsTask;
import com.example.android.popularmovies.Tasks.FetchTrailersTask;
import com.example.android.popularmovies.Utilities.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements FetchTrailersTask.Listener,
        TrailerListAdapter.Callbacks, LoaderManager.LoaderCallbacks<Cursor>, FetchReviewsTask.Listener {

    public static final String TAG = "MovieDetailFragment";

    @BindView(R.id.image_movie_detail_poster)
    ImageView movieImagePoster;
    @BindView(R.id.text_movie_original_title)
    TextView movieOriginalTitle;
    @BindView(R.id.text_movie_user_rating)
    TextView movieUserRating;
    @BindView(R.id.text_movie_release_date)
    TextView movieReleaseDate;
    @BindView(R.id.text_movie_overview)
    TextView movieOverview;

    @BindView(R.id.movie_videos)
    RecyclerView movieVideos;

    @BindView(R.id.movie_reviews)
    RecyclerView mRecyclerViewForReviews;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton fabButton;

    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;

    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    FavoritesService favoritesService;
    boolean isFavourited;

    int favouritedBackground;
    int notFavouritedBackground;

    private static final int ID_DETAIL_LOADER = 353;

    private Uri mUri;
    private long movieId;

    public static final String[] DETAIL_MOVIE_PROJECTION = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.COLUMN_TITLE,
            MoviesContract.COLUMN_SYNOPSE,
            MoviesContract.COLUMN_RELEASE_DATE,
            MoviesContract.COLUMN_POSTER_URL,
            MoviesContract.COLUMN_POPULARITY,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_SYNOPSE = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_POSTER_URL = 4;
    public static final int INDEX_MOVIE_POPULARITY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);
        mUri = getIntent().getData();

        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");
        movieId = MoviesContract.getIdFromUri(mUri);
        favouritedBackground = R.drawable.ic_favorite;
        notFavouritedBackground = R.drawable.ic_favorite_outline;
        favoritesService = new FavoritesService(this);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favoritesService.isFavorite(movieId)) {
                    favoritesService.removeFromFavorites(movieId);
                } else {
                    favoritesService.addToFavorites(movieId);
                }
                updateFab();
            }
        });

        mReviewListAdapter = new ReviewListAdapter(new ArrayList<Review>());
        mRecyclerViewForReviews.setAdapter(mReviewListAdapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieVideos.setLayoutManager(layoutManager);
        mTrailerListAdapter = new TrailerListAdapter(new ArrayList<Trailer>(), this);
        movieVideos.setAdapter(mTrailerListAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewListAdapter.add(reviews);
        } else {
            fetchReviews();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);
            movieVideos.setEnabled(true);
        } else {
            fetchTrailers();
        }

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
        updateFab();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty())
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);

        ArrayList<Review> reviews = mReviewListAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty())
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
    }

    private void updateFab() {
        if (favoritesService.isFavorite(movieId))
            fabButton.setImageResource(favouritedBackground);
        else
            fabButton.setImageResource(notFavouritedBackground);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        DETAIL_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null)
            return;
        data.moveToFirst();
        isFavourited = data.getExtras().getBoolean(Constants.TAG_FAVOURITED);

        if(isFavourited)
            fabButton.setImageResource(favouritedBackground);

        movieOriginalTitle.setText(data.getString(INDEX_MOVIE_TITLE));
        movieOverview.setText(data.getString(INDEX_MOVIE_SYNOPSE));
        movieReleaseDate.setText(data.getString(INDEX_MOVIE_RELEASE_DATE));
        movieUserRating.setText(data.getString(INDEX_MOVIE_POPULARITY) + getResources().getString(R.string.star_char));
        Picasso.with(this).load(Constants.POSTER_BASE_URL + data.getString(INDEX_MOVIE_POSTER_URL)).into(movieImagePoster);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}


    @Override
    public void onReviewsFetchFinished(List<Review> reviews) {
        mReviewListAdapter.add(reviews);
    }

    @Override
    public void onFetchFinished(List<Trailer> trailers) {
        mTrailerListAdapter.add(trailers);
        movieVideos.setEnabled(!trailers.isEmpty());
    }
    private void fetchTrailers() {
        FetchTrailersTask task = new FetchTrailersTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movieId);
    }

    private void fetchReviews() {
        FetchReviewsTask task = new FetchReviewsTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movieId);
    }

    @Override
    public void play(Trailer trailer, int position) {
        Intent playVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl()));
        startActivity(playVideo);
    }
}
