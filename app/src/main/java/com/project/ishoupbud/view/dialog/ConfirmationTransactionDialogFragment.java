package com.project.ishoupbud.view.dialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.DirectionsResult;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.Uber.UberAPI;
import com.project.ishoupbud.api.Uber.repositories.UberRepo;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.repositories.GoogleMapRepo;
import com.project.ishoupbud.manager.GoogleAPIManager;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.SelectLocationActivity;
import com.project.ishoupbud.view.activities.ShoppingCartActivity;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.PermissionsUtils;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by michael on 4/12/17.
 */

public class ConfirmationTransactionDialogFragment extends DialogFragment implements View.OnClickListener, OnMapReadyCallback {
    public final static String TAG = "tmp";

    Button btnSubmit;
    Button btnCancel;
    Button btnChangeLocation;
    TextView tvAddress;
    TextView tvDetail;
    EditText etAddress;

    private GoogleMap mMap;
    public double longitude;
    public double latitude;
    protected Location mLastLocation;
    public User user;
    public Vendor vendor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_confirm_transaction,container,false);
        getDialog().setTitle("Confirm Transaction");

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btnChangeLocation = (Button) rootView.findViewById(R.id.btn_change_location);
        tvAddress = (TextView) rootView.findViewById(R.id.tv_address);
        tvDetail = (TextView) rootView.findViewById(R.id.tv_detail_price);
        etAddress = (EditText) rootView.findViewById(R.id.et_df_address);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnChangeLocation.setOnClickListener(this);



        //TODO implement this class

        return rootView;
    }

    public void show(FragmentManager fm, String tag, User user, Vendor vendor) {
        this.user = user;
        this.vendor = vendor;
        super.show(fm, tag);
    }

    public void getPath(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("origin_lat", vendor.latitude);
        map.put("origin_lng", vendor.longitude);
        map.put("dest_lat", latitude);
        map.put("dest_lng", longitude);
        Call<GenericResponse<DirectionsResult>> getPathCall = APIManager.getRepository(GoogleMapRepo.class).getDirection(map);
        getPathCall.enqueue(new APICallback<GenericResponse<DirectionsResult>>() {
            @Override
            public void onSuccess(Call<GenericResponse<DirectionsResult>> call, retrofit2.Response<GenericResponse<DirectionsResult>> response) {
                super.onSuccess(call, response);
            }

            @Override
            public void onError(Call<GenericResponse<DirectionsResult>> call, retrofit2.Response<GenericResponse<DirectionsResult>> response) {
                super.onError(call, response);
            }

            @Override
            public void onFailure(Call<GenericResponse<DirectionsResult>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });

    }

    public void getEstimatePrice(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("start_latitude", vendor.latitude);
        map.put("start_longitude", vendor.longitude);
        map.put("end_latitude", latitude);
        map.put("end_longitude", longitude);
        Call<Response> getPrice = UberAPI.getRepository(UberRepo.class).getEstimatePrice(map);
        getPrice.enqueue(new APICallback<Response>() {
            @Override
            public void onSuccess(Call<Response> call, retrofit2.Response<Response> response) {
                super.onSuccess(call, response);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                super.onFailure(call, t);
            }

            @Override
            public void onError(Call<Response> call, retrofit2.Response<Response> response) {
                super.onError(call, response);
            }
        });
    }

    @Override
    public void onDestroyView() {
        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.remove(getFragmentManager().findFragmentByTag("googleMaps"));
        ft2.commit();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                ((ShoppingCartActivity)getActivity()).doCheckOut();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_change_location:
                Intent i = new Intent(getContext(),SelectLocationActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);

        LatLng sydney = new LatLng(3.637795, 98.687459);
        mMap.addMarker(new MarkerOptions().position(sydney));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        mMap.moveCamera(yourLocation);

        if(user.latitude == null){
            getCurrentLocation();
        }else{
            latitude = user.latitude;
            longitude = user.longitude;
            getEstimatePrice();
            getPath();
        }
    }

    public void getCurrentLocation(){
        setMycurrentPosition(true);
    }

    public void setMycurrentPosition(boolean status){
        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
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

                    //moving the map to location
                    moveMap();
                    getEstimatePrice();
                    getPath();
                }
            }
        } else {
            PermissionsUtils.shouldShowRequestPermission(getActivity(),PermissionsUtils.PERMISSION_LOCATION);
            Log.d(TAG, "getCurrentLocation: Permission");

            getEstimatePrice();
            getPath();
        }
    }

    private void moveMap() {
        Log.d(TAG, "moveMap: " + latitude + " " + longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.moveCamera(yourLocation);
        setMycurrentPosition(false);

    }
}
