package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.itderrickh.frolf.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        sharedPreferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = sharedPreferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button buttonRed = (Button) findViewById(R.id.buttonRed);
        Button buttonOrange = (Button) findViewById(R.id.buttonOrange);
        Button buttonYellow = (Button) findViewById(R.id.buttonYellow);
        Button buttonGreen = (Button) findViewById(R.id.buttonGreen);
        Button buttonBlue = (Button) findViewById(R.id.buttonBlue);
        Button buttonPurple = (Button) findViewById(R.id.buttonPurple);

        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppTheme, R.style.AppTheme_NoActionBar);
            }
        });

        buttonOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppOrangeTheme, R.style.AppOrangeTheme_NoActionBar);
            }
        });

        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppYellowTheme, R.style.AppYellowTheme_NoActionBar);
            }
        });

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppGreenTheme, R.style.AppGreenTheme_NoActionBar);
            }
        });

        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppBlueTheme, R.style.AppBlueTheme_NoActionBar);
            }
        });

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppPurpleTheme, R.style.AppPurpleTheme_NoActionBar);
            }
        });
    }

    public void recolor(int colorTheme, int colorNoBar) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("AppColor", colorTheme);
        editor.putInt("AppColorNoBar", colorNoBar);
        editor.apply();
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(main);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
