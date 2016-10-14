package com.itderrickh.frolf.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itderrickh.frolf.R;

import java.util.ArrayList;

/**
 * Created by derrickheinemann on 10/13/16.
 */
public class JoinGroupAdapter extends ArrayAdapter<Group> {
    public JoinGroupAdapter(Context context, int resource, ArrayList<Group> objects) {
        super(context, resource, objects);
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
        groupDistance.setText(group.getEmail());
        return convertView;
    }
}
