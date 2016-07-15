package com.cmrl.cmrllite;

/*
* This activity class will get the user location. The location so obtained will be passed on to the
* User login activity for storing. It uses 'fine' location finder and google play module
* B. Umesh rai
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private String latitude ="";
    private String longitude ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Start of the program. Builder is instantiated
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*
    * User grants the permission either when they install the app
    * (on Android 5.1 and lower) or while running the app (on Android 6.0 and higher).
     */
    @Override
    public void onConnected(Bundle bundle) { // Implemented Methods
        if (Build.VERSION.SDK_INT < 23) { // Lower than Marshmallow build
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else { // User to approve the permission
            if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        if (mLocation != null) {
            latitude = String.valueOf(mLocation.getLatitude());
            longitude = String.valueOf(mLocation.getLongitude());
            //Log.i("value","Latitude: " + latitude + " Longitude: " + longitude);
            // Pass location coordinate for user login
            Intent intent = new Intent(LocationActivity.this, UserActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            LocationActivity.this.startActivity(intent);
            finish();
        } else {
            // Request user to turn on Location
            displayLocationSettingsRequest(LocationActivity.this);
            //Log.i("value","Location not Detected");
        }

    }

    @Override
    public void onConnectionSuspended(int i) { // Implemented Methods
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { // Implemented Methods
        Log.i("value","Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() { // Overide start to start location finder method
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() { // Overide stop to stop location finder
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Pop up prompter to turn on Location service if off
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        final int REQUEST_CHECK_SETTINGS = 0x1;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //Log.i("view", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //Log.i("view", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            //Log.i("view", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Log.i("view", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}
