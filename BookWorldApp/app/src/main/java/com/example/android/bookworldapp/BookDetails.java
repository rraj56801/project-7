package com.example.android.bookworldapp;

/**
 * Created by RajBaba on 20-08-2017.
 */
public class BookDetails {
    private String mTitle;
    private String mSubtitle;
    private String mAuthors;
    private String mUrl;
    private int mPageCount;

    public BookDetails(String title, String subtitle, String authors, int pageCount, String url) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthors = authors;
        mUrl = url;
        mPageCount=pageCount;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getPageCount() {
        return mPageCount;
    }
}

