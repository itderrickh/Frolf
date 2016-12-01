package com.itderrickh.frolf.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;

import java.util.ArrayList;

public class GameFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);
        ImageView disc = (ImageView) findViewById(R.id.disc);

        ArrayList<Integer> scores = (ArrayList<Integer>)getIntent().getSerializableExtra("scores");

        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
                0.0f, 0.0f);
        animation.setDuration(5000);
        animation.setRepeatCount(5);
        animation.setRepeatMode(2);
        animation.setFillAfter(true);
        disc.startAnimation(animation);
    }
}
