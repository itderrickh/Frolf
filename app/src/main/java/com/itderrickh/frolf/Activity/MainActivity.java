package com.itderrickh.frolf.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ListView;
import android.widget.TextView;

import com.itderrickh.frolf.Fragments.ScoreFragment;
import com.itderrickh.frolf.Fragments.ScoreRowFragment;
import com.itderrickh.frolf.Helpers.FrontPageAdapter;
import com.itderrickh.frolf.Helpers.FrontPageItem;
import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.FriendService;
import com.itderrickh.frolf.Services.NotificationService;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    BroadcastReceiver receiver;
    Intent serviceIntent;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColorNoBar", R.style.AppTheme_NoActionBar);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation drawer stuff
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        boolean notifs = preferences.getBoolean("Notifications", false);

        if(notifs) {
            startNotificationService();
        }

        String token = preferences.getString("Auth_Token", "");
        String email = preferences.getString("Email", "");

        View header = navigationView.getHeaderView(0);
        TextView emailView = (TextView)header.findViewById(R.id.userEmail);
        final TextView homeEmptyText = (TextView) findViewById(R.id.homeEmptyText);

        //Set the email in the drawer
        emailView.setText(email);
        final ListView frontPageList = (ListView) findViewById(R.id.frontPageInfo);

        FriendService.getInstance().getFrontPageStats(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final ArrayList<FrontPageItem> frontPageItems = new ArrayList<FrontPageItem>();
                    String data = response.body().string();
                    JSONArray result = new JSONArray(data);
                    JSONObject row;
                    //Setup objects on the result
                    for (int i = 0; i < result.length(); i++) {
                        row = result.getJSONObject(i);

                        String dateRow = row.getString("datescored");

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date inputDate = dateFormat.parse(dateRow);
                        frontPageItems.add(new FrontPageItem(row.getInt("id"), inputDate, row.getString("groupname"), row.getInt("score"), row.getInt("par"), row.getInt("holes"), row.getString("email")));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FrontPageAdapter adapter = new FrontPageAdapter(getApplicationContext(), R.id.frontPageInfo, frontPageItems);
                            frontPageList.setAdapter(adapter);

                            if(frontPageItems.size() >= 1) {
                                homeEmptyText.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNotificationService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopNotificationService();
    }

    private void startNotificationService(){
        // check for if the service is already running
        if (!NotificationService.isRunning()) {

            if(serviceIntent == null) {
                serviceIntent = new Intent(this, NotificationService.class);
                serviceIntent.putExtra("token", preferences.getString("Auth_Token", ""));
            }

            startService(serviceIntent);
            registerReceiver(receiver, new IntentFilter("com.itderrickh.broadcast"));
        }
    }

    private void stopNotificationService() {
        if(NotificationService.isRunning() && receiver != null) {
            unregisterReceiver(receiver);
            stopService(serviceIntent);
        }
    }

    @Override
    public void onBackPressed() {
        //Handle closing the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
        } else if (id == R.id.action_statistics) {
            Intent statistics = new Intent(getApplicationContext(), StatisticsActivity.class);
            startActivity(statistics);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_creategroup) {
            //Start the create group page
            Intent createGroup = new Intent(getApplicationContext(), CreateGroupActivity.class);
            startActivity(createGroup);
        } else if (id == R.id.nav_logout) {
            //Log the user out and remove the auth token
            SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
            preferences.edit().remove("Auth_Token").remove("Email").commit();

            //Go to the login page
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
        } else if (id ==  R.id.nav_joingroup) {
            //Start the join group page
            Intent joinGroup = new Intent(getApplicationContext(), JoinGroupActivity.class);
            startActivity(joinGroup);
        } else if (id == R.id.nav_friends) {
            Intent friends = new Intent(getApplicationContext(), FriendsActivity.class);
            startActivity(friends);
        } else if (id == R.id.nav_add_friends) {
            Intent addFriends = new Intent(getApplicationContext(), AddFriendsActivity.class);
            startActivity(addFriends);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
