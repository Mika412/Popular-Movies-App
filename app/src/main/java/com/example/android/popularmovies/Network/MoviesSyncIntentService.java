package com.example.android.popularmovies.Network;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.popularmovies.Tasks.MoviesSyncTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MoviesSyncIntentService extends IntentService {

    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MoviesSyncTask.syncData(this);
    }
}