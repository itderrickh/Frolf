package com.itderrickh.frolf.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreFragment extends Fragment {
    private Score score;
    private int holeNumber;
    private ArrayList<Integer> scoreIds;
    private HashMap<Integer, Score> scores;

    private OnFragmentInteractionListener mListener;

    public ScoreFragment() {
        // Required empty public constructor
    }

    public static ScoreFragment newInstance(Score score, int holeNumber, HashMap<Integer, Score> scores, ArrayList<Integer> scoreIds) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putSerializable("score", score);
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
            score = (Score) getArguments().getSerializable("score");
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

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holeNumber--;

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScoreFragment scoreFragment = ScoreFragment.newInstance(scores.get(scoreIds.get(holeNumber)), holeNumber, scores, scoreIds);
                fragmentTransaction.replace(R.id.fragment_container, scoreFragment);
                fragmentTransaction.commit();

                //Save the score async
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holeNumber++;

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScoreFragment scoreFragment = ScoreFragment.newInstance(scores.get(scoreIds.get(holeNumber)), holeNumber, scores, scoreIds);
                fragmentTransaction.replace(R.id.fragment_container, scoreFragment);
                fragmentTransaction.commit();

                //Save the score async
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value++;
                score.setValue(value);
                scores.put(score.getId(), score);
                scoreField.setText(value + "");
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(scoreField.getText().toString());
                value--;
                score.setValue(value);
                scores.put(score.getId(), score);
                scoreField.setText(value + "");
            }
        });
        holeNum.setText("Hole " + (holeNumber + 1));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //if (context instanceof OnFragmentInteractionListener) {
        //   mListener = (OnFragmentInteractionListener) context;
        //} else {
        //    throw new RuntimeException(context.toString()
        //            + " must implement OnFragmentInteractionListener");
        //}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
