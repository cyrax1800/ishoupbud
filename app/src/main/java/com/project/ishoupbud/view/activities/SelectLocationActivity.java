package com.project.ishoupbud.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.ishoupbud.R;
import com.project.ishoupbud.manager.GoogleAPIManager;
import com.project.michael.base.utils.PermissionsUtils;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLocationActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar) Toolbar toolbar;

    private GoogleMap mMap;

    public double longitude;
    public double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Choose Position");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(3.537795, 98.687459);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        mMap.moveCamera(yourLocation);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = mMap.getCameraPosition();
                Log.d(TAG,"latidude "+ String.valueOf(cameraPosition.target.latitude));

                Log.d(TAG,"longitude "+ String.valueOf(cameraPosition.target.longitude));
            }
        });

        getCurrentLocation();
    }

    public void getCurrentLocation(){
        setMycurrentPosition(true);
    }

    public void setMycurrentPosition(boolean status){
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(status);
            if(status){
                Location location = LocationServices.FusedLocationApi.getLastLocation(GoogleAPIManager.getGoogleApi().getGoogleApiClient());
                Log.d(TAG, "getCurrentLocation: ");
                if (location != null) {
                    //Getting longitude and latitude
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();

                    //moving the map to location
                    moveMap();
                }
            }
        } else {
            PermissionsUtils.shouldShowRequestPermission(this,PermissionsUtils.PERMISSION_LOCATION);
            Log.d(TAG, "getCurrentLocation: Permission");
        }
    }

    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        Log.d(TAG, "moveMap: " + latitude + " " + longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.moveCamera(yourLocation);
        setMycurrentPosition(false);



    }
}
