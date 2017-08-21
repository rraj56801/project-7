package com.example.android.bookworldapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RajBaba on 20-08-2017.
 */
public class QueryUtility {
    public static final String LOG_TAG = BookActivity.class.getName();
    private static String mBook_title = null;
    private static String mBook_subtitle= null;
    private static int mBook_pageCount= 0;
    private static String mUrl=null;

   static ArrayList<String> mAuthor_names = new ArrayList<>();

    private QueryUtility() {
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            Log.v("QueryUtility", "Error response code:makeHttp");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<BookDetails> extractFeatureFromJson(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        List<BookDetails> bookDetailses = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                JSONObject accessInfo= currentBook.getJSONObject("accessInfo");
                if (volumeInfo.has("title")) {
                    mBook_title = volumeInfo.getString("title");
                }
                if (volumeInfo.has("subtitle")) {
                    mBook_subtitle = volumeInfo.getString("subtitle");
                }
                if (volumeInfo.has("authors")) {
                    JSONArray currentAuthorArray = volumeInfo.getJSONArray("authors");
                    int len = currentAuthorArray.length();
                    for (int j = 0; j < len; j++) {
                        mAuthor_names.add(currentAuthorArray.getString(j));
                    }
                }
                if (volumeInfo.has("pageCount")) {
                    mBook_pageCount = volumeInfo.getInt("pageCount");
                }
                if (accessInfo.has("webReaderLink")) {
                    mUrl = accessInfo.getString("webReaderLink");
                }
                BookDetails bookDetails = new BookDetails(mBook_title, mBook_subtitle, mAuthor_names.get(0), mBook_pageCount, mUrl);
                bookDetailses.add(bookDetails);
            }

        } catch (JSONException e) {
            Log.e("QueryUtility", "Problem parsing the bookJSON results", e);
        }
        return bookDetailses;
    }

    public static List<BookDetails> fetchBookData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<BookDetails> bookDetail = extractFeatureFromJson(jsonResponse);
        return bookDetail;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
}