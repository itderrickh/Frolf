package com.itderrickh.frolf.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itderrickh.frolf.Fragments.ScoreFragment;
import com.itderrickh.frolf.Helpers.OnSwipeTouchListener;
import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.ScoreService;

import org.json.JSONArray;
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
    private boolean firstReceive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);

        int hole = 0;
        if(savedInstanceState != null) {
            hole = savedInstanceState.getInt("holeNumber");
        }

        final int holeNumber = hole;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_score_land);
            getSupportActionBar().hide();
        } else {
            setContentView(R.layout.activity_score);
        }

        //Get the email for the user
        final String userEmail = preferences.getString("Email", "");

        scoreIds = new ArrayList<Integer>();

        this.groupId = getIntent().getIntExtra("groupId", 0);
        this.token = getIntent().getStringExtra("token");
        scores = new HashMap<>(18, 0.75f);

        //Handle updates from the score service
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String data = intent.getStringExtra("data");
                    JSONArray result = new JSONArray(data);
                    JSONObject row;
                    int column = 0;
                    String userRow;
                    scoreIds.clear();
                    //Setup objects on the result
                    for (int i = 0; i < result.length(); i++) {
                        row = result.getJSONObject(i);
                        String email = row.getString("email");

                        //Only keep scores for the current user
                        if(userEmail.equals(email)) {
                            scoreIds.add(row.getInt("id"));
                            scores.put(row.getInt("id"), new Score(row.getInt("id"), row.getInt("value"), row.getInt("user"), row.getInt("holeNum"), groupId));
                        }

                        //Handle logic for each row
                        if(i % 18 == 0) {
                            column++;
                            userRow = "user" + column;
                            TextView updateView = (TextView) findViewById(getResId(userRow, R.id.class));
                            try {

                                updateView.setText(email);
                            } catch(Exception e) { }
                        }

                        //Show the correct scores in each row
                        fillRowsWithScores(row, column, (i % 18) + 1);
                    }

                    //Handle showing the score fragment on data reception
                    if(scoreIds.size() > 0 && firstReceive && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        ScoreFragment scoreFragment = ScoreFragment.newInstance(holeNumber, scores, scoreIds, new ArrayList<Integer>());
                        fragmentTransaction.replace(R.id.fragment_container, scoreFragment, "SCORE");
                        fragmentTransaction.commit();
                        firstReceive = false;
                    }
                } catch (Exception ex) {
                    //Handle exception here
                    ex.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ScoreFragment scoreFragment = (ScoreFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        outState.putInt("holeNumber", scoreFragment.holeNumber);
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
