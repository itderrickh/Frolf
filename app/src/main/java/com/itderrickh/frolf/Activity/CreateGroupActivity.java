package com.itderrickh.frolf.Activity;

import android.Manifest;
import android.content.Intent;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            initializeLocationManager();
        }

        Button submitButton = (Button)findViewById(R.id.createGroup);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView groupNameField = (TextView) findViewById(R.id.groupName);
                String groupName = groupNameField.getText().toString();
                GroupService.getInstance().createGroup(groupName, location, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getApplicationContext(), "Unable to create group.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String groupId = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView groupNameField = (TextView) findViewById(R.id.groupName);
                                groupNameField.setText(groupId);
                            }
                        });
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
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException ex) {
                Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(locationManager != null && location == null) {
            try {
                locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
            } catch (SecurityException ex) {
                Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onProviderEnabled(String enabled) {}

    @Override
    public void onProviderDisabled(String enabled) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteriaForLocationService = new Criteria();
        criteriaForLocationService.setAccuracy(Criteria.ACCURACY_FINE);

        List<String> providers = locationManager.getProviders(criteriaForLocationService, true);
        String provider = "";
        if(providers.size() > 0) {
            provider = providers.get(0);
        }

        locationProvider = provider;

        try {
            locationManager.requestLocationUpdates(locationProvider, 400, 1, this);
            location = locationManager.getLastKnownLocation(locationProvider);

            //Kill the location service if we grabbed their location already.
            if(location != null) {
                locationManager.removeUpdates(this);
            }
        } catch (SecurityException ex) {
            Toast.makeText(getApplicationContext(), "Location disabled, please enable", Toast.LENGTH_SHORT).show();
        }
    }
}