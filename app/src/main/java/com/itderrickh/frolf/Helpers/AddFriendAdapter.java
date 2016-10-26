package com.itderrickh.frolf.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itderrickh.frolf.R;

import java.util.ArrayList;

public class AddFriendAdapter extends ArrayAdapter<GroupUser> {
    public AddFriendAdapter(Context context, int resource, ArrayList<GroupUser> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupUser groupUser = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_friends_row, parent, false);
        }

        TextView groupUserEmail = (TextView) convertView.findViewById(R.id.userEmail);

        groupUserEmail.setText(groupUser.getEmail());
        return convertView;
    }
}