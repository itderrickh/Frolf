package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.GroupService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class JoinGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        final String token = preferences.getString("Auth_Token", "");
        GroupService.getInstance().getGroupsNearMe(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Handle exception here
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                try {
                    JSONObject result = new JSONObject(data);

                    //Add the data to the list with an adapter
                } catch (Exception ex) { }
            }
        });
    }
}
