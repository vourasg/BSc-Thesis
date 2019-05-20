package com.icsd.municipapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewListAdapter extends ArrayAdapter<ReviewList>
{

    public ReviewListAdapter(Context context, ArrayList<ReviewList> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ReviewList reviewList = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_row, parent, false);
        // Lookup view for data population
        TextView type = (TextView) convertView.findViewById(R.id.type);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        ImageView streetView = (ImageView) convertView.findViewById(R.id.streetView);
        TextView address_tv = (TextView) convertView.findViewById(R.id.address_tv);
        // Populate the data into the template view using the data object
        type.setText(reviewList.display_text);
        date.setText(reviewList.date);
        streetView.setImageBitmap(reviewList.streetView);
        address_tv.setText(reviewList.address);
        // Return the completed view to render on screen
        return convertView;
    }
}