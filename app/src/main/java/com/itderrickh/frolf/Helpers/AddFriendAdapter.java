package com.itderrickh.frolf.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.FriendService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddFriendAdapter extends ArrayAdapter<GroupUser> {
    public AddFriendAdapter(Context context, int resource, ArrayList<GroupUser> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupUser groupUser = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_friends_row, parent, false);
        }

        final TextView groupUserEmail = (TextView) convertView.findViewById(R.id.userEmail);
        final ImageButton addButton = (ImageButton) convertView.findViewById(R.id.addFriendButton);
        SharedPreferences prefs = getContext().getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        final String token = prefs.getString("Auth_Token", "");

        addButton.setImageResource(R.drawable.ic_add);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add friend
                addButton.setImageResource(R.drawable.ic_done);
                FriendService.getInstance().addFriend(token, groupUser.getId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        addButton.setImageResource(R.drawable.ic_done);
                    }
                });
            }
        });

        groupUserEmail.setText(groupUser.getEmail());
        return convertView;
    }
}