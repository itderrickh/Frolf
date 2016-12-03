package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.itderrickh.frolf.Helpers.FriendUser;
import com.itderrickh.frolf.Helpers.FriendsAdapter;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.FriendService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String token = preferences.getString("Auth_Token", "");

        final ListView friendList = (ListView) findViewById(R.id.friendsList);

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendUser user = (FriendUser)friendList.getItemAtPosition(position);
                Toast.makeText(FriendsActivity.this, "Clicked item: " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });

        FriendService.getInstance().getFriends(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Handle exception
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Handle response here
                try {
                    final ArrayList<FriendUser> friendUsers = new ArrayList<FriendUser>();
                    String data = response.body().string();
                    JSONArray result = new JSONArray(data);
                    JSONObject row;
                    //Setup objects on the result
                    for (int i = 0; i < result.length(); i++) {
                        row = result.getJSONObject(i);
                        String dateRow = row.getString("dateadded");

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date inputDate = dateFormat.parse(dateRow);
                        int isPlayingInt = row.getInt("isplaying");
                        boolean isPlaying = (isPlayingInt == 1) ? true : false;
                        friendUsers.add(new FriendUser(row.getInt("id"), inputDate, row.getString("email"), isPlaying));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(friendUsers.size() > 0) {
                                FriendsAdapter adapter = new FriendsAdapter(getApplicationContext(), R.id.friendsList, friendUsers);
                                friendList.setAdapter(adapter);

                                LinearLayout friendsTarget = (LinearLayout) findViewById(R.id.friendsTarget);
                                friendsTarget.setVisibility(LinearLayout.GONE);
                            }


                        }
                    });
                } catch (Exception ex) {
                    //Handle exception here
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
