package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.itderrickh.frolf.R;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox receiveNotifications;
    private Button buttonRed;
    private Button buttonOrange;
    private Button buttonYellow;
    private Button buttonGreen;
    private Button buttonBlue;
    private Button buttonPurple;

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

        buttonRed = (Button) findViewById(R.id.buttonRed);
        buttonOrange = (Button) findViewById(R.id.buttonOrange);
        buttonYellow = (Button) findViewById(R.id.buttonYellow);
        buttonGreen = (Button) findViewById(R.id.buttonGreen);
        buttonBlue = (Button) findViewById(R.id.buttonBlue);
        buttonPurple = (Button) findViewById(R.id.buttonPurple);

        receiveNotifications = (CheckBox) findViewById(R.id.receiveNotifications);

        setupColorClicks();
        setupCheckBoxClick();
    }

    public void setupCheckBoxClick() {
        receiveNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receiveNotifications.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Notifications", true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Notifications", false);
                    editor.apply();
                }

                //Do work to start notifications
            }
        });
    }

    public void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }

    public void setupColorClicks() {
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppTheme, R.style.AppTheme_NoActionBar);
            }
        });
        AppCompatButton v = (AppCompatButton) buttonRed;
        ColorStateList csl = ColorStateList.valueOf(0xffA52422);
        v.setSupportBackgroundTintList(csl);

        buttonOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppOrangeTheme, R.style.AppOrangeTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonOrange;
        csl = ColorStateList.valueOf(0xffFF7E0B);
        v.setSupportBackgroundTintList(csl);

        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppYellowTheme, R.style.AppYellowTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonYellow;
        csl = ColorStateList.valueOf(0xffD8D800);
        v.setSupportBackgroundTintList(csl);

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppGreenTheme, R.style.AppGreenTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonGreen;
        csl = ColorStateList.valueOf(0xff007700);
        v.setSupportBackgroundTintList(csl);

        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppBlueTheme, R.style.AppBlueTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonBlue;
        csl = ColorStateList.valueOf(0xff1C47BB);
        v.setSupportBackgroundTintList(csl);

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppPurpleTheme, R.style.AppPurpleTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonPurple;
        csl = ColorStateList.valueOf(0xff6805a6);
        v.setSupportBackgroundTintList(csl);
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
