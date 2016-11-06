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

import com.itderrickh.frolf.Activity.ScoreActivity;
import com.itderrickh.frolf.Helpers.OnSwipeTouchListener;
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
    public int holeNumber;
    private ArrayList<Integer> scoreIds;
    private HashMap<Integer, Score> scores;
    private ArrayList<Integer> upToDateScores = new ArrayList<>();
    private EditText scoreField;
    private String token;
    private FragmentManager fragmentManager;
    private ScoreFragment previousFragment = null;
    private ScoreFragment nextFragment = null;

    public ScoreFragment() {
        // Required empty public constructor
    }

    public static ScoreFragment newInstance(int holeNumber, HashMap<Integer, Score> scores, ArrayList<Integer> scoreIds, ArrayList<Integer> upToDateScores) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putInt("holeNumber", holeNumber);
        args.putSerializable("scores", scores);
        args.putSerializable("scoreIds", scoreIds);
        args.putSerializable("upToDateScores", upToDateScores);
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
            upToDateScores = (ArrayList<Integer>)getArguments().getSerializable("upToDateScores");

            if(upToDateScores.size() == 0) {
                for(Integer key : scoreIds) {
                    upToDateScores.add(scores.get(key).getValue());
                }
            }
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
        scoreField = (EditText) getView().findViewById(R.id.scoreField);
        fragmentManager = getFragmentManager();

        //Setup previous hole like a linked list
        if(holeNumber >= 1) {
            previousFragment = ScoreFragment.newInstance(holeNumber - 1, scores, scoreIds, upToDateScores);
        } else {
            previousFragment = null;
        }

        //Setup next hole like a linked list
        if(holeNumber < 17) {
            nextFragment = ScoreFragment.newInstance(holeNumber + 1, scores, scoreIds, upToDateScores);
        } else {
            nextFragment = null;
        }

        //Get our preferences for auth and email
        SharedPreferences preferences = getActivity().getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        token = preferences.getString("Auth_Token", "");

        //Handle the previous click
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousHole();
            }
        });

        //Handle the next click
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextHole();
            }
        });

        getView().setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                previousHole();
            }

            public void onSwipeLeft() {
                nextHole();
            }
        });

        //Handle adding to the score
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value++;
                upToDateScores.set(holeNumber, value);

                scoreField.setText(value + "");
            }
        });

        //Handle subtracting the score
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value--;
                upToDateScores.set(holeNumber, value);

                scoreField.setText(value + "");
            }
        });

        //Set the hole number
        scoreField.setText(upToDateScores.get(holeNumber) + "");
        holeNum.setText("Hole " + (holeNumber + 1));
    }

    private void nextHole() {
        if(nextFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right);
            fragmentTransaction.replace(R.id.fragment_container, nextFragment);
            fragmentTransaction.commit();

            int newScore = Integer.parseInt(scoreField.getText().toString());

            //Save the score async
            GroupService.getInstance().updateScore(token, scores.get(scoreIds.get(holeNumber - 1)).getId(), newScore, new Callback() {
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
    }

    private void previousHole() {
        if(previousFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);
            fragmentTransaction.replace(R.id.fragment_container, previousFragment);
            fragmentTransaction.commit();

            int newScore = Integer.parseInt(scoreField.getText().toString());

            //Save the score async
            GroupService.getInstance().updateScore(token, scores.get(scoreIds.get(holeNumber + 1)).getId(), newScore, new Callback() {
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
    }
}
