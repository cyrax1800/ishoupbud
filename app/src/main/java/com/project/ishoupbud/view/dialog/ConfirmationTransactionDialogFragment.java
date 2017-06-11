package com.project.ishoupbud.view.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.view.activities.SelectLocationActivity;
import com.project.ishoupbud.view.activities.ShoppingCartActivity;

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
    }
}
