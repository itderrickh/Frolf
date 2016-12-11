package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.GPSTracker;
import com.itderrickh.frolf.Services.GroupService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateGroupActivity extends AppCompatActivity {

    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Get the shared pref for auth token
        final String token = preferences.getString("Auth_Token", "");

        gps = new GPSTracker(this);

        if(gps.getIsGPSTrackingEnabled()) {
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            //Set up the submit to create a group
            Button submitButton = (Button)findViewById(R.id.createGroup);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView groupNameField = (TextView) findViewById(R.id.groupName);
                    String groupName = groupNameField.getText().toString();

                    if(groupName.equals("")) {
                        Snackbar.make(findViewById(R.id.createGroupView), "Group name can not be blank", Snackbar.LENGTH_SHORT).show();
                    } else if(groupName.length() > 50) {
                        Snackbar.make(findViewById(R.id.createGroupView), "Group name can not longer than 50 characters", Snackbar.LENGTH_SHORT).show();
                    } else {
                        //Call the service to make the group
                        GroupService.getInstance().createGroup(token, groupName, latitude, longitude, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(getApplicationContext(), "Unable to create group.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final int groupId = Integer.parseInt(response.body().string());
                                Intent score = new Intent(getApplicationContext(), ScoreActivity.class);
                                score.putExtra("groupId", groupId);
                                score.putExtra("token", token);
                                score.putExtra("isLeader", true);
                                startActivity(score);
                            }
                        });
                    }
                }
            });

            gps.stopUsingGPS();
        } else {
            Toast.makeText(this, "Please enable GPS and restart the application.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(gps != null) {
            gps.stopUsingGPS();
        }
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