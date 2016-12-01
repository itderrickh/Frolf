package com.itderrickh.frolf.Activity;

import android.app.Fragment;
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

import com.itderrickh.frolf.Fragments.ScoreFragment;
import com.itderrickh.frolf.Fragments.ScoreRowFragment;
import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.ScoreService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreActivity extends AppCompatActivity implements ScoreFragment.OnGameFinishedInterface {

    BroadcastReceiver receiver;
    Intent serviceIntent;
    private int groupId;
    private String token;
    private HashMap<Integer, Score> scores;
    private ArrayList<Integer> scoreIds;
    private HashMap<String, ScoreRowFragment> scoreRows;
    private boolean firstReceive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            scoreRows = (HashMap<String, ScoreRowFragment>)savedInstanceState.getSerializable("scoreRows");
        } else {
            scoreRows = new HashMap<>(10, 0.75f);
        }


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
                    scoreIds.clear();

                    //Setup objects on the result
                    for (int i = 0; i < result.length();) {
                        ArrayList<Score> userScores = new ArrayList<>();
                        String email = "";
                        for(int c = i; c < i + 18; c++) {
                            row = result.getJSONObject(c);
                            email = row.getString("email");

                            Score rowScore = new Score(row.getInt("id"), row.getInt("value"), row.getInt("user"), row.getInt("holeNum"), groupId);
                            if(userEmail.equals(email)) {
                                scoreIds.add(row.getInt("id"));
                                scores.put(row.getInt("id"), rowScore);
                            }

                            userScores.add(rowScore);
                        }

                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            if(scoreRows.containsKey(email)) {
                                scoreRows.get(email).updateScores(userScores);
                            } else {
                                ScoreRowFragment srf = ScoreRowFragment.newInstance(userScores, email);
                                scoreRows.put(email, srf);
                                fragmentTransaction.add(R.id.scoreTarget, srf, "SCORE_ROW" + email);
                            }

                            fragmentTransaction.commit();
                        }

                        i += 18;
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
    public void OnGameFinished(ArrayList<Integer> scores) {
        Intent finishGame = new Intent(getApplicationContext(), GameFinishedActivity.class);
        finishGame.putExtra("scores", scores);
        startActivity(finishGame);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ScoreFragment scoreFragment = (ScoreFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        outState.putInt("holeNumber", scoreFragment.holeNumber);
        outState.putSerializable("scoreRows", scoreRows);
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
