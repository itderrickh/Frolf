package com.itderrickh.frolf.Services;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginService {
    private static LoginService me;
    private static OkHttpClient client;
    private static String LOGIN_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/login.php";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private LoginService() { }

    public static LoginService getInstance() {
        if(me == null) {
            me = new LoginService();
            client = new OkHttpClient();
        }

        return me;
    }

    public Call login(String email, String password, Callback callback) {
        JSONObject jsonBuilder = new JSONObject();
        String json = "{}";

        try {
            jsonBuilder.put("email", email)
                    .put("password", password);
            json = jsonBuilder.toString();
        } catch (JSONException ex) {
            //TODO: handle exception
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }
}
