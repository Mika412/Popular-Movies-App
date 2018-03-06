package com.example.android.popularmovies.Utilities;

import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Contains network based functions.
 */

public class NetworkUtils {

    /** Put your API KEY */
    public static String API_KEY = "";


    /** Builds and returns the query url, with option of mostpopular or toprated passed through sort parameter. */

    public static URL buildURL(String sort){
        Uri uri = Uri.parse(Constants.QUERY_BASE_URL + sort).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    /** Queries JSON data from a given url, and returns it as a string. */

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }
}
