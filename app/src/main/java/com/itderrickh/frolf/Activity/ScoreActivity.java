package com.itderrickh.frolf.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.ScoreService;

import org.json.JSONException;
import org.json.JSONObject;

public class ScoreActivity extends AppCompatActivity {

    BroadcastReceiver receiver;
    Intent serviceIntent;
    private int groupId;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        this.groupId = getIntent().getIntExtra("groupId", 0);
        this.token = getIntent().getStringExtra("token");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String data = intent.getStringExtra("data");
                    JSONObject result = new JSONObject(data);
                } catch (JSONException ex) {

                }
            }
        };
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
