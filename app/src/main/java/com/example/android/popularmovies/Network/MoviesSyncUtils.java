package com.example.android.popularmovies.Network;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class MoviesSyncUtils {
    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MoviesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}