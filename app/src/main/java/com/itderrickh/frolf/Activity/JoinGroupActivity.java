package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.itderrickh.frolf.Helpers.Group;
import com.itderrickh.frolf.Helpers.JoinGroupAdapter;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.GroupService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class JoinGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final ListView groupList = (ListView) findViewById(R.id.groupList);

        //Get the auth token
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        final String token = preferences.getString("Auth_Token", "");

        //Make a call to get the groups near us
        GroupService.getInstance().getGroupsNearMe(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Handle exception here
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                //TODO: handle groups that are far away client side
                try {
                    final ArrayList<Group> groups = new ArrayList<>();
                    Group groupRow;
                    JSONArray result = new JSONArray(data);
                    for(int i = 0; i < result.length(); i++) {
                        JSONObject row = result.getJSONObject(i);
                        groupRow = new Group(
                                    row.getInt("id"),
                                    row.getString("name"),
                                    row.getDouble("latitude"),
                                    row.getDouble("longitude"),
                                    row.getString("email")
                        );

                        groups.add(groupRow);
                    }

                    //We should setup the list, click, and progress bar on the UI thread after getting groups
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JoinGroupAdapter adapter = new JoinGroupAdapter(getApplicationContext(), R.layout.group_row, groups);
                            groupList.setAdapter(adapter);
                            progressBar.setVisibility(View.INVISIBLE);
                            groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    final int groupId = groups.get(arg2).getId();

                                    //Call service to join group
                                    GroupService.getInstance().joinGroup(token, groupId, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            //TODO: handle failure
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            Intent score = new Intent(getApplicationContext(), ScoreActivity.class);
                                            score.putExtra("groupId", groupId);
                                            score.putExtra("token", token);
                                            startActivity(score);
                                        }
                                    });
                                }
                            });
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
