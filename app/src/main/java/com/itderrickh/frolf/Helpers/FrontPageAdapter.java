package com.itderrickh.frolf.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itderrickh.frolf.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FrontPageAdapter extends ArrayAdapter<FrontPageItem> {
    public FrontPageAdapter(Context context, int resource, ArrayList<FrontPageItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final FriendUser friendUser = getItem(position);
        final FrontPageItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.front_page_row, parent, false);
        }

        final TextView friendEmail = (TextView) convertView.findViewById(R.id.frontPageEmail);

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = df.format(item.getDatescored());

        friendEmail.setText(item.getEmail() + " scored " +
                            item.getScore() + " on " +
                            dateString + " with the group " +
                            item.getGroupName());
        return convertView;
    }
}