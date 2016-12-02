package com.itderrickh.frolf.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

    private CheckBox receiveNotifications;
    private Button buttonRed;
    private Button buttonOrange;
    private Button buttonYellow;
    private Button buttonGreen;
    private Button buttonBlue;
    private Button buttonPurple;
    private Button snapPicture;
    private ImageView profileImage;

    private String profileImageURL;
    private File newfile;

    private SharedPreferences sharedPreferences;
    private String directory;
    private String imageUUID;
    private int TAKE_PHOTO_CODE = 0;
    private static final int MY_REQUEST_CODE = 5;

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

        receiveNotifications = (CheckBox) findViewById(R.id.receiveNotifications);

        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/frolfImages/";
        File newdir = new File(directory);
        newdir.mkdirs();

        // Check permission for CAMERA
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_REQUEST_CODE);
        } else {
            // permission has been granted, continue as usual
            setupSnapPictureClick();
        }

        setupColorClicks();
        setupCheckBoxClick();
    }

    public void setupCheckBoxClick() {
        receiveNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receiveNotifications.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Notifications", true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Notifications", false);
                    editor.apply();
                }

                //Do work to start notifications
            }
        });
    }

    public void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupSnapPictureClick();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void setupSnapPictureClick() {
        snapPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UUID id = UUID.randomUUID();
                imageUUID = id.toString();
                String file = directory + imageUUID + ".jpg";
                newfile = new File(file);
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
    }

    public void setupColorClicks() {
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppTheme, R.style.AppTheme_NoActionBar);
            }
        });
        AppCompatButton v = (AppCompatButton) buttonRed;
        ColorStateList csl = ColorStateList.valueOf(0xffA52422);
        v.setSupportBackgroundTintList(csl);

        buttonOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppOrangeTheme, R.style.AppOrangeTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonOrange;
        csl = ColorStateList.valueOf(0xffFF7E0B);
        v.setSupportBackgroundTintList(csl);

        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppYellowTheme, R.style.AppYellowTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonYellow;
        csl = ColorStateList.valueOf(0xffD8D800);
        v.setSupportBackgroundTintList(csl);

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppGreenTheme, R.style.AppGreenTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonGreen;
        csl = ColorStateList.valueOf(0xff007700);
        v.setSupportBackgroundTintList(csl);

        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppBlueTheme, R.style.AppBlueTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonBlue;
        csl = ColorStateList.valueOf(0xff1C47BB);
        v.setSupportBackgroundTintList(csl);

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recolor(R.style.AppPurpleTheme, R.style.AppPurpleTheme_NoActionBar);
            }
        });
        v = (AppCompatButton) buttonPurple;
        csl = ColorStateList.valueOf(0xff6805a6);
        v.setSupportBackgroundTintList(csl);
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

        String file = directory + imageUUID + ".jpg";
        String token = sharedPreferences.getString("Auth_Token", "");

        FileUploadService.getInstance().uploadFile(token, newfile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(directory + imageUUID + ".jpg");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //DO something here
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileImage.setImageURI(Uri.parse("http://webdev.cs.uwosh.edu/students/heined50/FrolfBackend/uploads/" + imageUUID + ".jpg" ));
                    }
                });
            }
        });

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            Log.d("CameraDemo", "Pic saved");
        }
    }
}
