package com.itderrickh.frolf.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.itderrickh.frolf.Activity.MainActivity;
import com.itderrickh.frolf.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationService extends Service {
    Updater updater;
    BroadcastReceiver broadcaster;
    Intent intent;
    public static final String BROADCAST_ACTION = "com.itderrickh.notification";
    public static final String DATA_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/getFriends.php";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private HashMap<String, Boolean> isNotified;
    private static boolean isRunning = false;
    private static final String ACTION_STOP_SELF = "com.itderrickh.notification.STOP_SERVICE";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updater = new Updater();
        isNotified = new HashMap<>();
        initBroadcastReciever();

        intent = new Intent(BROADCAST_ACTION);
        isRunning = true;
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        if (!updater.isRunning()) {
            updater.token = intent.getStringExtra("token");
            updater.start();
            updater.isRunning = true;
        }

        return START_STICKY;
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

    private void initBroadcastReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP_SELF);
        registerReceiver(mBroadcastReciever,intentFilter);
    }

    class Updater extends Thread {

        public boolean isRunning = false;
        public long DELAY = 4000;
        public String token;

        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();
            String json = "{}";

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
                        try {
                            JSONArray results = new JSONArray(response.body().string());

                            for(int i = 0; i < results.length(); i++) {
                                JSONObject row = results.getJSONObject(i);
                                boolean isPlaying = (row.getInt("isplaying") == 1) ? true : false;
                                boolean hasNotBeenNotified = true;

                                if(isNotified.containsKey(row.getString("email"))) {
                                    hasNotBeenNotified = !isNotified.get(row.getString("email"));
                                }

                                if(hasNotBeenNotified && isPlaying) {
                                    sendResult(row.getString("email"));
                                    isNotified.put(row.getString("email"), true);
                                }
                            }
                        } catch (Exception ex) { }
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
        PushNotification(data);
    }

    public void PushNotification(String data) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Frolf").setContentText(data + " is playing now!")
                .setSmallIcon(R.drawable.hole).getNotification();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    private BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendResult(intent.getStringExtra("data"));
        }
    };

    public static boolean isRunning() {
        return isRunning;
    }
}
