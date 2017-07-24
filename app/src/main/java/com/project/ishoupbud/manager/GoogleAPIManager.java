package com.project.ishoupbud.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.project.ishoupbud.view.activities.SelectLocationActivity;
import com.project.michael.base.utils.PermissionsUtils;

import java.lang.ref.WeakReference;

/**
 * Created by michael on 4/14/17.
 */

public class GoogleAPIManager implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    public final String TAG = "tmp google API";
    public static final int REQUEST_CODE_RESOLUTION = 1;
    private static GoogleAPIManager instance;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private WeakReference<AppCompatActivity> activityRef;

    public void initGoogleAPI(AppCompatActivity activity){
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        this.activityRef = new WeakReference<>(activity);
        mGoogleApiClient.connect();
        Log.d(TAG, "initGoogleAPI: ");
    }

    public GoogleApiClient getGoogleApiClient(){
        if(mGoogleApiClient == null){
            throw new IllegalStateException("google client need to be initialize");
        }
        else{
            return mGoogleApiClient;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: " + getGoogleApiClient().isConnected());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(activityRef.get(), connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(activityRef.get(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            throw new IllegalStateException(e);
        }
    }

    public LatLng getCurrentPosition(Activity activity){
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }else {
            PermissionsUtils.shouldShowRequestPermission(activity,PermissionsUtils.PERMISSION_LOCATION);
            mLastLocation = null;
            Log.d(TAG, "getCurrentLocation: Permission");
            return null;
        }
        return new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }

    public static synchronized GoogleAPIManager getGoogleApi() {
        if(instance == null){
            instance = new GoogleAPIManager();
        }
        return instance;
    }
}
