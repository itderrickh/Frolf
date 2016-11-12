package com.itderrickh.frolf.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itderrickh.frolf.R;
import com.itderrickh.frolf.Services.GroupService;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateGroupActivity extends AppCompatActivity implements LocationListener {

    private Location location;
    private LocationManager locationManager;
    private String locationProvider;
    private final int REQUEST_PERMISSION_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get our preferences for auth and email
        SharedPreferences preferences = getSharedPreferences("FROLF_SETTINGS", Context.MODE_PRIVATE);
        int appColor = preferences.getInt("AppColor", R.style.AppTheme);
        setTheme(appColor);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //We have to check for permissions before we use the location
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            initializeLocationManager();
        }

        //Get the shared pref for auth token
        final String token = preferences.getString("Auth_Token", "");

        //Set up the submit to create a group
        Button submitButton = (Button)findViewById(R.id.createGroup);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView groupNameField = (TextView) findViewById(R.id.groupName);
                String groupName = groupNameField.getText().toString();
                //Call the service to make the group
                GroupService.getInstance().createGroup(token, groupName, location, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getApplicationContext(), "Unable to create group.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final int groupId = Integer.parseInt(response.body().string());
                        Intent score = new Intent(getApplicationContext(), ScoreActivity.class);
                        score.putExtra("groupId", groupId);
                        score.putExtra("token", token);
                        startActivity(score);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //Make sure to pause the location updates
        if(locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException ex) {
                Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException ex) {
                Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //Resume getting the location
        if(locationManager != null && location == null) {
            try {
                locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
            } catch (SecurityException ex) {
                Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException ex) {
                Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onProviderEnabled(String enabled) {}

    @Override
    public void onProviderDisabled(String enabled) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onLocationChanged(Location location) {
        //If the location changes, update it then stop looking for updates
        // This should save some battery life instead of tracking the location of the group
        try {
            this.location = location;
            locationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Handle the permissions
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeLocationManager();
                } else {
                    Toast.makeText(this, "Activate permission to use this feature!", Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(this, MainActivity.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main);
                }
        }
    }

    public void initializeLocationManager() {
        //Call upon the location services
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteriaForLocationService = new Criteria();
        criteriaForLocationService.setAccuracy(Criteria.ACCURACY_FINE);

        //Select the first available provider
        List<String> providers = locationManager.getProviders(criteriaForLocationService, true);
        Location bestLocation = null;

        try {
            //Search for those updates!
            //locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
            //location = locationManager.getLastKnownLocation(locationProvider);

            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            location = bestLocation;

            //Kill the location service if we grabbed their location already.
            if(location != null) {
                locationManager.removeUpdates(this);
            }


        } catch (SecurityException ex) {
            Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            //TODO: make sure they update location settings
            Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
        }
    }
}