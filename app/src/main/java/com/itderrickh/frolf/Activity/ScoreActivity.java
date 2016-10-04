package com.itderrickh.frolf.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.ScoreService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class ScoreActivity extends AppCompatActivity {

    BroadcastReceiver receiver;
    Intent serviceIntent;
    private int groupId;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_score_land);
            getSupportActionBar().hide();
        } else {
            setContentView(R.layout.activity_score);
        }

        this.groupId = getIntent().getIntExtra("groupId", 0);
        this.token = getIntent().getStringExtra("token");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String data = intent.getStringExtra("data");
                    JSONArray result = new JSONArray(data);
                    JSONObject row;
                    for (int i = 0; i < result.length(); i++) {
                        row = result.getJSONObject(i);
                        fillRowsWithScores(row, i, (i + 1) / 18);
                    }
                } catch (Exception ex) {
                    //Handle exception here
                }
            }
        };
    }

    private void fillRowsWithScores(JSONObject row, int index, int rowNum) {
        int colNum = index + 1;
        TextView updateView = (TextView) findViewById(getResId("score" + colNum + "" + rowNum, R.id.class));
        try {
            updateView.setText(row.getString("value"));
        } catch(Exception e) { }
    }

    private int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        serviceIntent = new Intent(getApplicationContext(), ScoreService.class);
        serviceIntent.putExtra("groupId", this.groupId);
        serviceIntent.putExtra("token", this.token);
        startService(serviceIntent);

        registerReceiver(receiver, new IntentFilter("com.itderrickh.broadcast"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(serviceIntent);
        unregisterReceiver(receiver);
    }
}
