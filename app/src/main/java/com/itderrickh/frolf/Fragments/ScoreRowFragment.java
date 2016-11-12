package com.itderrickh.frolf.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itderrickh.frolf.Helpers.Score;
import com.itderrickh.frolf.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoreRowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreRowFragment extends Fragment {

    private ArrayList<Score> scores;
    private String username;
    private ArrayList<TextView> scoresView;
    private TextView usernameView;
    public ScoreRowFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param scores Parameter 1.
     * @return A new instance of fragment ScoreRow.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreRowFragment newInstance(ArrayList<Score> scores, String username) {
        ScoreRowFragment fragment = new ScoreRowFragment();
        Bundle args = new Bundle();
        args.putSerializable("scores", scores);
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            scores = (ArrayList<Score>)getArguments().getSerializable("scores");
            username = getArguments().getString("username");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        scoresView = new ArrayList<>();

        usernameView = (TextView) getView().findViewById(R.id.userHeader);
        usernameView.setText(username);

        for(int i = 0; i < 18; i++) {
            String viewName = "score" + (i + 1);
            TextView updateView = (TextView) getView().findViewById(getResId(viewName, R.id.class));

            updateView.setText(scores.get(i).getValue() + "");
            scoresView.add(updateView);
        }
    }

    public void updateScores(ArrayList<Score> newScores) {
        scores = newScores;
        for(int i = 0; i < 18; i++) {
            String viewName = "score" + (i + 1);
            TextView updateView = (TextView) getView().findViewById(getResId(viewName, R.id.class));

            updateView.setText(scores.get(i).getValue() + "");
            scoresView.add(updateView);
        }
    }

    private int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score_row, container, false);
    }

}
