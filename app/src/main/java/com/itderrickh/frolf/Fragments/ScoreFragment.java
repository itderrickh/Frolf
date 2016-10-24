package com.itderrickh.frolf.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.GroupService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ScoreFragment extends Fragment {
    private int holeNumber;
    private ArrayList<Integer> scoreIds;
    private HashMap<Integer, Score> scores;

    public ScoreFragment() {
        // Required empty public constructor
    }

    public static ScoreFragment newInstance(int holeNumber, HashMap<Integer, Score> scores, ArrayList<Integer> scoreIds) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putInt("holeNumber", holeNumber);
        args.putSerializable("scores", scores);
        args.putSerializable("scoreIds", scoreIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            holeNumber = getArguments().getInt("holeNumber");
            scores = (HashMap<Integer, Score>)getArguments().getSerializable("scores");
            scoreIds = (ArrayList<Integer>)getArguments().getSerializable("scoreIds");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        final TextView holeNum = (TextView) getView().findViewById(R.id.holeNum);
        final ImageButton prev = (ImageButton) getView().findViewById(R.id.prevButton);
        final ImageButton next = (ImageButton) getView().findViewById(R.id.nextButton);
        final ImageButton plus = (ImageButton) getView().findViewById(R.id.addButton);
        final ImageButton minus = (ImageButton) getView().findViewById(R.id.subtractButton);
        final EditText scoreField = (EditText) getView().findViewById(R.id.scoreField);

        //Get our preferences for auth and email
        SharedPreferences preferences = getActivity().getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        final String token = preferences.getString("Auth_Token", "");

        //Handle the previous click
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holeNumber--;

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScoreFragment scoreFragment = ScoreFragment.newInstance(holeNumber, scores, scoreIds);
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container, scoreFragment);
                fragmentTransaction.commit();

                //Save the score async
            }
        });

        //Handle the next click
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holeNumber++;

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScoreFragment scoreFragment = ScoreFragment.newInstance(holeNumber, scores, scoreIds);
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right);
                fragmentTransaction.replace(R.id.fragment_container, scoreFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //Save the score async
                GroupService.getInstance().updateScore(token, scores.get(scoreIds.get(holeNumber)).getValue(), scores.get(scoreIds.get(holeNumber)).getId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Handle error
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Do nothing we updated successfully
                    }
                });
            }
        });

        //Handle adding to the score
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value++;
                scores.get(scoreIds.get(holeNumber)).setValue(value);
                scoreField.setText(value + "");
            }
        });

        //Handle subtracting the score
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value--;
                scores.get(scoreIds.get(holeNumber)).setValue(value);
                scoreField.setText(value + "");
            }
        });

        //Set the hole number
        scoreField.setText(scores.get(scoreIds.get(holeNumber)).getValue() + "");
        holeNum.setText("Hole " + (holeNumber + 1));
    }
}
