package com.itderrickh.frolf.Services;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by heined50 on 9/26/2016.
 */
public class GroupService {
    private static GroupService me;
    private static OkHttpClient client;
    private static String CREATE_GROUP_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/createGroup.php";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private GroupService() { }

    public static GroupService getInstance() {
        if(me == null) {
            me = new GroupService();
            client = new OkHttpClient();
        }

        return me;
    }

    public Call createGroup(String token, String groupName, Location loc, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("groupName", groupName)
                    .put("latitude", loc.getLatitude())
                    .put("longitude", loc.getLongitude());
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(CREATE_GROUP_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }
}
