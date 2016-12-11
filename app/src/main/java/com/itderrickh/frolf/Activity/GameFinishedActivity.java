package com.itderrickh.frolf.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itderrickh.frolf.R;

import java.util.ArrayList;

public class GameFinishedActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);
        ImageView disc = (ImageView) findViewById(R.id.disc);
        TextView finishedText = (TextView) findViewById(R.id.finishedText);

        ArrayList<Integer> scores = (ArrayList<Integer>)getIntent().getSerializableExtra("scores");
        int totalScore = 0;
        for(Integer i : scores) {
            totalScore += i;
        }
        
        finishedText.setText("You scored " + totalScore + " on 18 holes!");

        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
                0.0f, 0.0f);
        animation.setDuration(1000);
        animation.setRepeatCount(10);
        animation.setRepeatMode(2);
        animation.setFillAfter(true);
        disc.startAnimation(animation);

        Button goHome = (Button) findViewById(R.id.goHome);
        goHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }
}
