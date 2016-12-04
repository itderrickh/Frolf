package com.itderrickh.frolf.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itderrickh.frolf.R;

import org.w3c.dom.Text;

import java.util.List;

public class StatisticsAdapter extends ArrayAdapter<Statistic> {

    public StatisticsAdapter(Context context, int resource, List<Statistic> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Statistic stat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.statistic_row, parent, false);
        }

        TextView statDesc = (TextView) convertView.findViewById(R.id.statText);
        TextView statNumber = (TextView) convertView.findViewById(R.id.statNumber);

        statDesc.setText(stat.getDescription());
        statNumber.setText(stat.getStat() + "");

        return convertView;
    }
}
