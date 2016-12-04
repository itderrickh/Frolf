package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.itderrickh.frolf.Helpers.Statistic;
import com.itderrickh.frolf.Helpers.StatisticsAdapter;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.FriendService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        String token = preferences.getString("Auth_Token", "");
        int userId = getIntent().getIntExtra("userId", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView statList = (ListView) findViewById(R.id.statList);

        FriendService.getInstance().getStatistics(token, userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                try {
                    ArrayList<Statistic> stats = new ArrayList<Statistic>();
                    JSONArray result = new JSONArray(data);
                    for(int i = 0; i < result.length(); i++) {
                        JSONObject row = result.getJSONObject(i);
                        Statistic stat = new Statistic(row.getString("description"), row.getDouble("stat"));

                        stats.add(stat);
                    }

                    final StatisticsAdapter adapter = new StatisticsAdapter(getApplicationContext(), R.id.statList, stats);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statList.setAdapter(adapter);

                            //Hide after we get statistics
                            LinearLayout statisticsTarget = (LinearLayout) findViewById(R.id.statisticsTarget);
                            statisticsTarget.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception ex) {
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
