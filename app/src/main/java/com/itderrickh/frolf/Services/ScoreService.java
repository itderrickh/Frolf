package com.itderrickh.frolf.Services;

import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScoreService extends Service {

    Updater updater;
    BroadcastReceiver broadcaster;
    Intent intent;
    public static final String BROADCAST_ACTION = "com.itderrickh.broadcast";
    public static final String DATA_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getGroupScores.php";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updater = new Updater();

        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {

        if (!updater.isRunning()) {
            updater.groupId = intent.getIntExtra("groupId", 0);
            updater.token = intent.getStringExtra("token");
            updater.start();
            updater.isRunning = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();

        if (updater.isRunning) {
            updater.interrupt();
            updater.isRunning = false;
            updater = null;
        }

    }

    class Updater extends Thread {

        public boolean isRunning = false;
        public long DELAY = 4000;
        public int groupId;
        public String token;

        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonBuilder = new JSONObject();
            String json = "{}";

            try {
                jsonBuilder.put("groupId", groupId);
                json = jsonBuilder.toString();
            } catch (JSONException ex) {
                //TODO: handle exception
            }

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(DATA_URL)
                    .addHeader("Authorize", this.token)
                    .post(body)
                    .build();

            isRunning = true;
            while (isRunning) {
                //Do work to get data here

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //TODO: handle exceptions
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        sendResult(response.body().string());
                    }
                });

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isRunning = false;
                }
            } // while end
        } // run end

        public boolean isRunning() {
            return this.isRunning;
        }

    } // inner class end

    public void sendResult(String data) {
        intent.putExtra("data", data);
        sendBroadcast(intent);
    }
} // outer class end