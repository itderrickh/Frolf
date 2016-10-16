package com.itderrickh.frolf.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.ScoreService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ScoreActivity extends AppCompatActivity {

    BroadcastReceiver receiver;
    Intent serviceIntent;
    private int groupId;
    private String token;
    private HashMap<Integer, Score> scores;
    private ArrayList<Integer> scoreIds;
    private Integer currentScore;
    private int holeIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_score_land);
            getSupportActionBar().hide();
        } else {
            setContentView(R.layout.activity_score);
        }

        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        final String userEmail = preferences.getString("Email", "");

        scoreIds = new ArrayList<Integer>();

        //Stuff for landscape
        this.groupId = getIntent().getIntExtra("groupId", 0);
        this.token = getIntent().getStringExtra("token");
        scores = new HashMap<>(18, 0.75f);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String data = intent.getStringExtra("data");
                    JSONArray result = new JSONArray(data);
                    JSONObject row;
                    int column = 0;
                    String userRow;
                    for (int i = 0; i < result.length(); i++) {
                        row = result.getJSONObject(i);
                        String email = row.getString("email");
                        if(userEmail.equals(email)) {
                            scoreIds.add(row.getInt("id"));
                            scores.put(row.getInt("id"), new Score(row.getInt("id"), row.getInt("value"), row.getInt("user"), row.getInt("holeNum"), groupId));
                        }

                        if(i % 18 == 0) {
                            column++;
                            userRow = "user" + column;
                            TextView updateView = (TextView) findViewById(getResId(userRow, R.id.class));
                            try {

                                updateView.setText(email);
                            } catch(Exception e) { }
                        }

                        fillRowsWithScores(row, column, (i % 18) + 1);
                    }

                    if(scoreIds.size() > 0) {
                        currentScore = scoreIds.get(0);
                    }
                } catch (Exception ex) {
                    //Handle exception here
                }
            }
        };

        final ImageButton prev = (ImageButton) findViewById(R.id.prevButton);
        final ImageButton next = (ImageButton) findViewById(R.id.nextButton);
        final ImageButton plus = (ImageButton) findViewById(R.id.addButton);
        final ImageButton minus = (ImageButton) findViewById(R.id.subtractButton);
        final EditText scoreField = (EditText) findViewById(R.id.scoreField);
        final TextView holeNum = (TextView) findViewById(R.id.holeNum);
        holeNum.setText("Hole " + (holeIndex + 1));

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holeIndex--;
                holeNum.setText("Hole " + (holeIndex + 1));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holeIndex++;
                holeNum.setText("Hole " + (holeIndex + 1));
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value++;
                scoreField.setText(value + "");
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value--;
                scoreField.setText(value + "");
            }
        });
    }

    private void fillRowsWithScores(JSONObject row, int colNum, int rowNum) {
        String viewName = "score" + colNum + String.format("%02d", rowNum);
        TextView updateView = (TextView) findViewById(getResId(viewName, R.id.class));
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
