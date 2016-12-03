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

public class FriendsAdapter extends ArrayAdapter<FriendUser> {
    public FriendsAdapter(Context context, int resource, ArrayList<FriendUser> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FriendUser friendUser = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_row, parent, false);
        }

        final TextView friendEmail = (TextView) convertView.findViewById(R.id.friendEmail);
        final TextView friendDate = (TextView) convertView.findViewById(R.id.friendDate);
        final TextView isPlaying = (TextView) convertView.findViewById(R.id.isPlaying);

        friendEmail.setText(friendUser.getEmail());

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String reportDate = df.format(friendUser.getDateAdded());

        isPlaying.setText((friendUser.isplaying()) ? "Playing" : "");
        friendDate.setText(reportDate);
        return convertView;
    }
}