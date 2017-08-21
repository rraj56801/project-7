package com.example.android.bookworldapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookDetails>> {
    /**
     * URL for google books data from the Google API
     */
    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private static String mURL = null;
    private static String mBookTitleSearched = null;
    private static String mBookAuthorSearched = null;
    public static final String LOG_TAG = BookActivity.class.getName();
    private BookAdapter mAdapter;
    private static final int BOOK_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private EditText booktitle_editText;
    private EditText bookauthor_editText;
    private Button search_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booktitle_editText = (EditText) findViewById(R.id.search_title);
        bookauthor_editText = (EditText) findViewById(R.id.search_author);
        search_button = (Button) findViewById(R.id.search_button);
        ListView bookListView = (ListView) findViewById(R.id.list);
        mAdapter = new BookAdapter(BookActivity.this, new ArrayList<BookDetails>());
        bookListView.setAdapter(mAdapter);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        if (networkInfo == null || networkInfo.isFailover()) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        search_button.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 LoaderManager loaderManager = getLoaderManager();
                                                 mBookTitleSearched = (booktitle_editText.getText().toString().toLowerCase()).trim();
                                                 mBookAuthorSearched = (bookauthor_editText.getText().toString().toLowerCase()).trim();
                                                 ListView bookListView = (ListView) findViewById(R.id.list);
                                                 bookListView.setEmptyView(mEmptyStateTextView);
                                                 mAdapter = new BookAdapter(BookActivity.this, new ArrayList<BookDetails>());
                                                 bookListView.setAdapter(mAdapter);
                                                 ConnectivityManager connMgr = (ConnectivityManager)
                                                         getSystemService(Context.CONNECTIVITY_SERVICE);
                                                 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                                 loadingIndicator.setVisibility(View.VISIBLE);
                                                 if (networkInfo != null && networkInfo.isConnected()) {
                                                     if (mBookTitleSearched.length() < 5 && mBookAuthorSearched.length() < 5) {
                                                         mEmptyStateTextView.setText(R.string.no_search_msg);
                                                     }
                                                     if (mBookTitleSearched.length() > 0 && mBookAuthorSearched.length() > 0) {
                                                         mEmptyStateTextView.setText(R.string.search_hint);
                                                     }
                                                     if (!mBookTitleSearched.equals("")) {
                                                         mURL = GOOGLE_BOOKS_REQUEST_URL + mBookTitleSearched.replaceAll("\\s", "");
                                                     }
                                                     if (!mBookAuthorSearched.equals("")) {
                                                         mURL = GOOGLE_BOOKS_REQUEST_URL + mBookAuthorSearched.replaceAll("\\s", "");
                                                     }
                                                     loaderManager.initLoader(BOOK_LOADER_ID, null, BookActivity.this);
                                                 } else {
                                                     loadingIndicator.setVisibility(View.GONE);
                                                     mEmptyStateTextView.setText(R.string.no_internet_connection);
                                                 }
                                                 loaderManager.restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
                                             }
                                         }

        );

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                            {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                    BookDetails currentBook = mAdapter.getItem(position);
                                                    Uri bookUri = Uri.parse(currentBook.getUrl());
                                                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                                                    startActivity(websiteIntent);
                                                }
                                            }

        );
    }

    @Override
    public Loader<List<BookDetails>> onCreateLoader(int id, Bundle args) {
        Log.v("Loader", mURL);
        return new BookLoader(this, mURL);
    }

    @Override
    public void onLoadFinished(Loader<List<BookDetails>> loader, List<BookDetails> books) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_books);
        mAdapter.clear();
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookDetails>> loader) {
        mAdapter.clear();
    }
}
