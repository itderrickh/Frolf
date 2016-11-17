package com.itderrickh.frolf.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by derrickheinemann on 11/7/16.
 */
public class FileUploadService {
    private static FileUploadService me;
    private static OkHttpClient client;
    private static final String UPLOAD_URL = "http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/upload.php";
    public static final MediaType MULTIPART_FORM = MediaType.parse("application/x-www-form-urlencoded");

    private FileUploadService() { }

    public static FileUploadService getInstance() {
        if(me == null) {
            me = new FileUploadService();
            client = new OkHttpClient();
        }

        return me;
    }

    public Call uploadFile(String token, File file, Callback callback) {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MULTIPART_FORM, file))
                .build();
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .addHeader("Authorize", token)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }
}