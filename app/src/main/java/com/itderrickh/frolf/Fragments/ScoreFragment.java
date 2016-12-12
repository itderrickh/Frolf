package com.itderrickh.frolf.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itderrickh.frolf.Helpers.InputFilterMinMax;
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
    private Button finishGame;
    private FragmentManager fragmentManager;
    private ScoreFragment previousFragment = null;
    private ScoreFragment nextFragment = null;
    private OnGameFinishedInterface listener;

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
        finishGame = (Button) getView().findViewById(R.id.finishGame);
        scoreField = (EditText) getView().findViewById(R.id.scoreField);
        fragmentManager = getFragmentManager();

        scoreField.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "10")});

        if(holeNumber == 17) {
            finishGame.setVisibility(View.VISIBLE);
        } else {
            finishGame.setVisibility(View.GONE);
        }

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

        prev.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousFragment = ScoreFragment.newInstance(0, scores, scoreIds, upToDateScores);
                previousHole();

                return true;
            }
        });

        //Handle the next click
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextHole();
            }
        });

        next.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nextFragment = ScoreFragment.newInstance(17, scores, scoreIds, upToDateScores);
                nextHole();

                return true;
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

                if(value < 10) {
                    value++;
                    upToDateScores.set(holeNumber, value);

                    scoreField.setText(value + "");
                }
            }
        });

        //Handle subtracting the score
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());

                if(value != 0) {
                    value--;
                    upToDateScores.set(holeNumber, value);

                    scoreField.setText(value + "");
                }
            }
        });

        finishGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.OnGameFinished(upToDateScores);
                }
            }
        });

        //Set the hole number
        scoreField.setText(upToDateScores.get(holeNumber) + "");
        holeNum.setText("Hole " + (holeNumber + 1));
    }

    private void nextHole() {
        if(nextFragment != null) {
            String scoreFieldText = scoreField.getText().toString();

            if(!scoreFieldText.equals("")) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right);
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

                int newScore = Integer.parseInt(scoreFieldText);

                //Save the score async
                GroupService.getInstance().updateScore(token, scores.get(scoreIds.get(holeNumber)).getId(), newScore, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Handle error
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Do nothing we updated successfully
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Score field is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previousHole() {
        if(previousFragment != null) {
            String scoreFieldText = scoreField.getText().toString();

            if(!scoreFieldText.equals("")) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container, previousFragment);
                fragmentTransaction.commit();

                int newScore = Integer.parseInt(scoreFieldText);

                //Save the score async
                GroupService.getInstance().updateScore(token, scores.get(scoreIds.get(holeNumber)).getId(), newScore, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Handle error
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Do nothing we updated successfully
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Score field is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnGameFinishedInterface) {
            listener = (OnGameFinishedInterface) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameFinishedInterface) {
            listener = (OnGameFinishedInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnGameFinishedInterface {
        void OnGameFinished(ArrayList<Integer> scores);
    }
}
