package com.example.android.popularmovies.Utilities;

import android.util.Log;


/**
 * Contains miscellaneous functions that can be used throughout the project
 */
public class GeneralUtilities {
        private static Boolean logEnabled = true;

        /** A log class that that can be toggled to show or hide the log prints */

        public static class Logger{
                public static void d(String TAG, String string){ if(logEnabled)Log.d(TAG, string);}
                public static void e(String TAG, String string){ if(logEnabled)Log.e(TAG, string);}
                public static void i(String TAG, String string){ if(logEnabled)Log.i(TAG, string);}
        }
}
