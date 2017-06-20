package com.project.ishoupbud.view.dialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.Uber.UberAPI;
import com.project.ishoupbud.api.Uber.model.UberPrice;
import com.project.ishoupbud.api.Uber.model.UberPrices;
import com.project.ishoupbud.api.Uber.repositories.UberRepo;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.model.map.Direction;
import com.project.ishoupbud.api.model.map.DirectionsStep;
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
import com.project.michael.base.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    Polyline line;
    Marker marker;
    Marker vendorMarker;
    LatLngBounds latLngBounds;

    public String location;
    public String distance;
    public int priceValue;
    public String duration;

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
        Call<Direction> getPathCall = APIManager.getRepository(GoogleMapRepo.class).getDirection(map);
        getPathCall.enqueue(new APICallback<Direction>() {
            @Override
            public void onSuccess(Call<Direction> call, retrofit2.Response<Direction> response) {
                super.onSuccess(call, response);
                List<LatLng> listLatLang = new ArrayList<>();
                DirectionsStep[] steps = response.body().routes[0].legs[0].steps.clone();
                for(int i = 0; i < steps.length; i++){
                    List<com.google.maps.model.LatLng> modelLatLng = steps[i].polyline.decodePath();
                    for(int j = 0; j < modelLatLng.size(); j++){
                        listLatLang.add(new LatLng(modelLatLng.get(j).lat,modelLatLng.get(j).lng));
                    }
                }
                if(line != null){
                    line.remove();
                }
                line = mMap.addPolyline(new PolylineOptions()
                .addAll(listLatLang).width(2).color(Color.RED)
                );
                latLngBounds = new LatLngBounds(new LatLng(response.body().routes[0].bounds.southwest.lat,response.body().routes[0].bounds.southwest.lng),
                        new LatLng(response.body().routes[0].bounds.northeast.lat,response.body().routes[0].bounds.northeast.lng));
                distance = response.body().routes[0].legs[0].distance.text;
                duration = response.body().routes[0].legs[0].durationInTraffic.text;
                location = response.body().routes[0].legs[0].startAddress;
                updateText();
                moveMap();
            }

            @Override
            public void onError(Call<Direction> call, retrofit2.Response<Direction> response) {
                super.onError(call, response);
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                super.onFailure(call, t);
            }
        });

    }

    public boolean validate(){
        location = etAddress.getText().toString();
        if(location.isEmpty()){
            Toast.makeText(getContext(),"Lokasi gk boleh kosong",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void updateText(){
        tvDetail.setText("ETA: " + duration +
                "\nCourier Service: " + Utils.indonesiaFormat(priceValue) +
                "\nTotal: " + Utils.indonesiaFormat((((ShoppingCartActivity)getActivity()).totalPrice + priceValue))
                + "\nCurrent Balance: " + user.saldo);

        etAddress.setText(location);
    }

    public void getEstimatePrice(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("start_latitude", vendor.latitude);
        map.put("start_longitude", vendor.longitude);
        map.put("end_latitude", latitude);
        map.put("end_longitude", longitude);
        Call<UberPrices> getPrice = UberAPI.getRepository(UberRepo.class).getEstimatePrice(map);
        getPrice.enqueue(new APICallback<UberPrices>() {
            @Override
            public void onSuccess(Call<UberPrices> call, retrofit2.Response<UberPrices> response) {
                super.onSuccess(call, response);
                UberPrices data = response.body();
                priceValue = Integer.MAX_VALUE;
                for(int i = 0; i< data.prices.size();i++){
                    priceValue = Math.min(data.prices.get(0).lowEstimate, priceValue);
                }
                updateText();
            }

            @Override
            public void onFailure(Call<UberPrices> call, Throwable t) {
                super.onFailure(call, t);
            }

            @Override
            public void onError(Call<UberPrices> call, retrofit2.Response<UberPrices> response) {
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
                location = etAddress.getText().toString();
                if(validate()){
                    ((ShoppingCartActivity)getActivity()).doCheckOut();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_change_location:
                Intent i = new Intent(getContext(),SelectLocationActivity.class);
                getActivity().startActivityForResult(i, ShoppingCartActivity.REQUEST_MAP);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);

        LatLng sydney = new LatLng(3.637795, 98.687459);
        marker = mMap.addMarker(new MarkerOptions().position(sydney));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        mMap.moveCamera(yourLocation);

        if(user.latitude == null || user.longitude == null){
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
        if(marker != null) marker.remove();
        LatLng position = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(position));

        if(vendorMarker != null) vendorMarker.remove();
        LatLng vendorPosition = new LatLng(vendor.latitude, vendor.longitude);
        vendorMarker = mMap.addMarker(new MarkerOptions().position(vendorPosition));

        CameraUpdate yourLocation;

        if(latLngBounds != null){
            yourLocation = CameraUpdateFactory.newLatLngBounds(latLngBounds, 100);
        }else{
            LatLng latLng = new LatLng(latitude, longitude);
            yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        }

        mMap.moveCamera(yourLocation);
        setMycurrentPosition(false);

    }
}
