package com.itderrickh.frolf.Services;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

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
        String json = "{\"email\": " + email  +", \"password\": " + password + "}";
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
