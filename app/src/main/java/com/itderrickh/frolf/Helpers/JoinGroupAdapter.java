package com.itderrickh.frolf.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itderrickh.frolf.R;

import java.util.ArrayList;

public class JoinGroupAdapter extends ArrayAdapter<Group> {

    SharedPreferences prefs;
    int distance = 10;
    public JoinGroupAdapter(Context context, int resource, ArrayList<Group> objects) {
        super(context, resource, objects);
        prefs = context.getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        distance = prefs.getInt("ScanDistance", 10);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Group group = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_row, parent, false);
        }

        TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
        TextView groupDistance = (TextView) convertView.findViewById(R.id.groupDistance);

        groupName.setText(group.getName());

        if(group.isCurrentLocationSet() && group.isLocationSet()) {
            if(group.getDistance() > distance) {
                convertView.setVisibility(View.GONE);
            } else {
                groupDistance.setText(String.format("%.2f mi", group.getDistance()));
            }
        } else {
            groupDistance.setText("Unknown distance");
        }

        return convertView;
    }
}
