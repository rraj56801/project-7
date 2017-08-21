package com.example.android.bookworldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RajBaba on 20-08-2017.
 */
public class BookAdapter extends ArrayAdapter<BookDetails> {
    public BookAdapter(Context context, List<BookDetails> books) {

        super(context, 0, books);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        BookDetails currentBook = getItem(position);
        TextView currentBookTitle = (TextView) listItemView.findViewById(R.id.title_text_view);
        currentBookTitle.setText(currentBook.getTitle());
        TextView currentBookSubTitle = (TextView) listItemView.findViewById(R.id.subtitle_text_view);
        currentBookSubTitle.setText(currentBook.getSubtitle());
        TextView currentBookAuthor = (TextView) listItemView.findViewById(R.id.author_text_view);
        currentBookAuthor.setText(currentBook.getAuthors());
        TextView currentBookPageCount = (TextView) listItemView.findViewById(R.id.pagecount_text_view);
        currentBookPageCount.setText("Pages:" + String.valueOf(currentBook.getPageCount()));
        return listItemView;
    }

}
