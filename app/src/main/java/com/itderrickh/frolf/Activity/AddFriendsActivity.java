package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.itderrickh.frolf.Helpers.AddFriendAdapter;
import com.itderrickh.frolf.Helpers.GroupUser;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.GroupService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        //Get the shared pref for auth token
        final String token = preferences.getString("Auth_Token", "");

        final ListView list = (ListView) findViewById(R.id.addFriendsList);
        GroupService.getInstance().getRecentGroupmates(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Handle response here
                try {
                    final ArrayList<GroupUser> groupUsers = new ArrayList<GroupUser>();
                    String data = response.body().string();
                    JSONArray result = new JSONArray(data);
                    JSONObject row;
                    //Setup objects on the result
                    for (int i = 0; i < result.length(); i++) {
                        row = result.getJSONObject(i);
                        groupUsers.add(new GroupUser(row.getInt("id"), row.getString("email")));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AddFriendAdapter adapter = new AddFriendAdapter(getApplicationContext(), R.id.addFriendsList, groupUsers);
                            list.setAdapter(adapter);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
