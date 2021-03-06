package com.itderrickh.frolf.Services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GroupService {
    private static GroupService me;
    private static OkHttpClient client;
    private static final String CREATE_GROUP_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/createGroup.php";
    private static final String GET_GROUPS_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getGroups.php";
    private static final String JOIN_GROUP_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/joinGroup.php";
    private static final String UPDATE_SCORE_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/updateScore.php";
    private static final String GET_GROUPMATES_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getRecentGroupmates.php";
    private static final String FINISH_GAME_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/finishGame.php";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private GroupService() { }

    public static GroupService getInstance() {
        if(me == null) {
            me = new GroupService();
            client = new OkHttpClient();
        }

        return me;
    }

    public Call createGroup(String token, String groupName, double latitude, double longitude, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("groupName", groupName)
                    .put("latitude", latitude)
                    .put("longitude", longitude);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle exception
        } catch (NullPointerException ex) {
            //TODO: handle missing longitude and latitude
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

    public Call joinGroup(String token, int groupId, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("groupId", groupId);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle json exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(JOIN_GROUP_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call getGroupsNearMe(String token, Callback callback) {
        Request request = new Request.Builder()
                .url(GET_GROUPS_URL)
                .addHeader("Authorize", token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call finishGame(String token, int groupId, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("groupId", groupId);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle json exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(FINISH_GAME_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call updateScore(String token, int scoreId, int score, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("scoreId", scoreId);
            jsonBuilder.put("score", score);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle json exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(UPDATE_SCORE_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call getRecentGroupmates(String token, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(GET_GROUPMATES_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }
}
