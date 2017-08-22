package com.project.ishoupbud.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
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
import com.project.ishoupbud.service.FetchAddressService;
import com.project.ishoupbud.utils.ConstClass;
import com.project.michael.base.utils.PermissionsUtils;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.views.BaseActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLocationActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    Toolbar toolbar;
    TextView tvAddress;
    Button btn_submit;
    EditText etSearchLocation;

    private GoogleMap mMap;

    public double longitude;
    public double latitude;
    public boolean isRequestedLocation;

    protected String mAddressOutput;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private Boolean isFromSelectAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        isFromSelectAddress = false;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        etSearchLocation = (EditText) findViewById(R.id.et_search_location);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mResultReceiver = new AddressResultReceiver(new Handler());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        etSearchLocation.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
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
                longitude = cameraPosition.target.longitude;
                latitude = cameraPosition.target.latitude;
                mLastLocation.setLatitude(latitude);
                mLastLocation.setLongitude(longitude);
//                Log.d(TAG,"latidude "+ String.valueOf(latitude));
//                Log.d(TAG,"longitude "+ String.valueOf(longitude));
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(isRequestedLocation) return;
                if(isFromSelectAddress){
                    isFromSelectAddress = false;
                    return;
                }
                isRequestedLocation = true;
                startIntentService();
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
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(GoogleAPIManager.getGoogleApi().getGoogleApiClient());
                Log.d(TAG, "getCurrentLocation: ");
                if (mLastLocation != null) {
                    //Getting longitude and latitude
                    longitude = mLastLocation.getLongitude();
                    latitude = mLastLocation.getLatitude();
                    mLastLocation.setLatitude(latitude);
                    mLastLocation.setLongitude(longitude);
                    startIntentService();

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
        CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(yourLocation);

        setMycurrentPosition(false);

    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressService.class);
        intent.putExtra(FetchAddressService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressService.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public void updateAddressText(){
        if(StringUtils.isNullOrEmpty(mAddressOutput)) return;
        tvAddress.setText(mAddressOutput);
        isRequestedLocation = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.et_search_location:
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .setCountry("ID")
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            case R.id.btn_submit:
                if(mAddressOutput == null || mAddressOutput.equals("no address")) return;
                Intent data = new Intent();
                data.putExtra(ConstClass.ADDRESS_EXTRA, mAddressOutput);
                data.putExtra(ConstClass.LATITUDE_EXTRA, latitude);
                data.putExtra(ConstClass.LONGITUDE_EXTRA, longitude);
                setResult(-1, data);//Success
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                isFromSelectAddress = true;
                LatLng lokasi = place.getLatLng();
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(lokasi, 15);
                mMap.animateCamera(yourLocation);

                longitude = lokasi.longitude;
                latitude = lokasi.latitude;
                mLastLocation.setLatitude(latitude);
                mLastLocation.setLongitude(longitude);
                mAddressOutput = place.getAddress().toString();
                etSearchLocation.setText(place.getName());
                updateAddressText();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(FetchAddressService.RESULT_DATA_KEY);
            updateAddressText();

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressService.SUCCESS_RESULT) {
//                Toast.makeText(getApplicationContext(),"address found",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
