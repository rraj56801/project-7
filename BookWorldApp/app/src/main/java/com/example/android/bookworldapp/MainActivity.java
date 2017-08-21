package com.example.android.bookworldapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * URL for google books data from the Google API
     */
    private static final String GOOGLE_BOOKS_REQUEST_URL  =
            "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=";

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * Returns true if network is available or about to become available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<BookDetails>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Set a click listener on the search Button, to implement the search
        Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the button is clicked on.
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                //Check for internet connection
                if (isNetworkAvailable(context)) {
                    EditText searchEditTextView = (EditText) findViewById(R.id.search);
                    String searchValue= searchEditTextView.getText().toString();
                    if (searchValue.isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.no_search_msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),R.string.search_msg + searchValue, Toast.LENGTH_SHORT).show();
                        String url = GOOGLE_BOOKS_REQUEST_URL  + searchValue;
                        // Start the AsyncTask to fetch the book data
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute(url);
                    }

                } else {
                    //Provide feedback about no internet connection
                    Toast.makeText(MainActivity.this,R.string.connection_msg, Toast.LENGTH_LONG).show();

                }
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                BookDetails currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of books in the response.
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return a Book.
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class BookAsyncTask extends AsyncTask<String, Void, List<BookDetails>> {
        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link BookDetails}s as the result.
         */
        ProgressDialog progressDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDailog= new ProgressDialog(MainActivity.this);
            progressDailog.setMessage(getResources().getString(R.string.progress_msg));
            progressDailog.setIndeterminate(false);
            progressDailog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDailog.setCancelable(true);
            progressDailog.show();
        }

        @Override
        protected List<BookDetails> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<BookDetails> result = QueryUtility.fetchBookData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of book data from a previous
         * query to Google Books API. Then we update the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<BookDetails> data) {
            // Clear the adapter of previous book data
            mAdapter.clear();
            progressDailog.dismiss();
            // If there is a valid list of {@link Book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}


