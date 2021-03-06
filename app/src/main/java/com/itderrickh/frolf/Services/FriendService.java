package com.itderrickh.frolf.Services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FriendService {
    private static FriendService me;
    private static OkHttpClient client;
    private static final String ADD_FRIEND_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/addFriend.php";
    private static final String GET_FRONT_PAGE_STATS = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getFrontPageStats.php";
    private static final String GET_FRIENDS_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getFriends.php";
    private static final String GET_STATS_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getStatistics.php";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private FriendService() { }

    public static FriendService getInstance() {
        if(me == null) {
            me = new FriendService();
            client = new OkHttpClient();
        }

        return me;
    }

    public Call getStatistics(String token, int userId, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("userId", userId);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle json exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(GET_STATS_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call addFriend(String token, int userId, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("friendId", userId);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle json exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(ADD_FRIEND_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call getFriends(String token,  Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(GET_FRIENDS_URL)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call getFrontPageStats(String token, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(GET_FRONT_PAGE_STATS)
                .addHeader("Authorize", token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }
}
