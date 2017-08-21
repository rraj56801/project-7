package com.example.android.bookworldapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by RajBaba on 20-08-2017.
 */
public class BookLoader extends AsyncTaskLoader<List<BookDetails>> {

    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<BookDetails> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.e("bookLoader","doInBackground");
        List<BookDetails> books= QueryUtility.fetchBookData(mUrl);
        return books;
    }
}
