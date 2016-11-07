package com.itderrickh.frolf.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.FileUploadService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonRed;
    private Button buttonOrange;
    private Button buttonYellow;
    private Button buttonGreen;
    private Button buttonBlue;
    private Button buttonPurple;
    private Button snapPicture;
    private ImageView profileImage;

    private String profileImageURL;

    private SharedPreferences sharedPreferences;
    private String directory;
    private String imageUUID;
    private int TAKE_PHOTO_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        sharedPreferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = sharedPreferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonRed = (Button) findViewById(R.id.buttonRed);
        buttonOrange = (Button) findViewById(R.id.buttonOrange);
        buttonYellow = (Button) findViewById(R.id.buttonYellow);
        buttonGreen = (Button) findViewById(R.id.buttonGreen);
        buttonBlue = (Button) findViewById(R.id.buttonBlue);
        buttonPurple = (Button) findViewById(R.id.buttonPurple);
        snapPicture = (Button) findViewById(R.id.snapPicture);

        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/frolfImages/";
        File newdir = new File(directory);
        newdir.mkdirs();

        snapPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UUID id = UUID.randomUUID();
                imageUUID = id.toString();
                String file = directory + imageUUID + ".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e) { }

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });

        setupColorClicks();
    }

    public void setupColorClicks() {
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppTheme, R.style.AppTheme_NoActionBar);
            }
        });

        buttonOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppOrangeTheme, R.style.AppOrangeTheme_NoActionBar);
            }
        });

        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppYellowTheme, R.style.AppYellowTheme_NoActionBar);
            }
        });

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppGreenTheme, R.style.AppGreenTheme_NoActionBar);
            }
        });

        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppBlueTheme, R.style.AppBlueTheme_NoActionBar);
            }
        });

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppPurpleTheme, R.style.AppPurpleTheme_NoActionBar);
            }
        });
    }

    public void recolor(int colorTheme, int colorNoBar) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("AppColor", colorTheme);
        editor.putInt("AppColorNoBar", colorNoBar);
        editor.apply();
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(main);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //profileImage.setImageURI(new Uri.Builder().);
        String file = directory + imageUUID + ".jpg";
        File fileToUpload = new File(file);
        String token = sharedPreferences.getString("Auth_Token", "");

        FileUploadService.getInstance().uploadFile(token, fileToUpload, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Handle exception
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //DO something here
            }
        });

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            Log.d("CameraDemo", "Pic saved");
        }
    }
}
