package com.project.ishoupbud.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.Uber.UberAPI;
import com.project.ishoupbud.api.Uber.model.UberPrices;
import com.project.ishoupbud.api.Uber.repositories.UberRepo;
import com.project.ishoupbud.api.model.FetchingShipmentDataHelper;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.model.map.Direction;
import com.project.ishoupbud.api.model.map.DirectionsStep;
import com.project.ishoupbud.api.repositories.GoogleMapRepo;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.manager.GoogleAPIManager;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductTransactionAdapter;
import com.project.ishoupbud.view.dialog.AddEditReviewDialogFragment;
import com.project.ishoupbud.view.fragment.TransactionFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.PermissionsUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.listeners.ClickEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/14/17.
 */

public class DetailTransactionActivity extends BaseActivity implements OnMapReadyCallback {

    public Transaction transaction;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nestedScroll) NestedScrollView nestedScrollView;
    @BindView(R.id.tv_no_transaction) TextView tvNoTransaction;
    @BindView(R.id.tv_status) TextView tvStatus;
    @BindView(R.id.tv_total_price) TextView tvPrice;
    @BindView(R.id.tv_estimate_time) TextView tvETA;
    @BindView(R.id.tv_distance) TextView tvDistance;
    @BindView(R.id.rv_product) RecyclerView rvProduct;
    @BindView(R.id.btn_terima_barang) Button btnBarangTerima;
    ProductTransactionAdapter<ShoppingCart> productTransactionAdapter;
    private GoogleMap mMap;

    Polyline line;
    Marker marker;
    Marker vendorMarker;
    LatLngBounds latLngBounds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        ButterKnife.bind(this);

        transaction = GsonUtils.getObjectFromJson(getIntent().getStringExtra(
                ConstClass.TRANSACTION_EXTRA), Transaction.class);

        toolbar.setTitle(transaction.detail.get(0).vendor.vendor.name);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (transaction.status == 0) {
            btnBarangTerima.setVisibility(View.VISIBLE);
            tvStatus.setText("Status: in progress");
            getPath(true);
        } else if (transaction.status == 1) {
            btnBarangTerima.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                    nestedScrollView.getLayoutParams();
            marginLayoutParams.setMargins(0, 0, 0, 0);
            nestedScrollView.setLayoutParams(marginLayoutParams);
            tvStatus.setText("Status: Complete");
            getPath(false);
        }

        btnBarangTerima.setOnClickListener(this);

        productTransactionAdapter = new ProductTransactionAdapter<>(this);
        productTransactionAdapter.setItemListener(R.id.btn_ulas_barang,
                new ClickEventListener<ShoppingCart>() {
                    @Override
                    public void onClick(View v, ShoppingCart shoppingCart, int position) {
                        showUlasBarang(shoppingCart.product.id, shoppingCart.vendor.vendor.id);
                    }
                });
        productTransactionAdapter.setNew(transaction.detail);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        rvProduct.addItemDecoration(new InsetDividerItemDecoration(this));
        rvProduct.setLayoutManager(layoutManager);
        rvProduct.setAdapter(productTransactionAdapter);

        tvNoTransaction.setText("No. Transaction: " + transaction.id);
        tvPrice.setText("Total Price: " + Utils.indonesiaFormat(transaction.nominal));


    }

    public void getPath(final boolean animate) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("origin_lat", transaction.detail.get(0).vendor.vendor.latitude);
        map.put("origin_lng", transaction.detail.get(0).vendor.vendor.longitude);
        map.put("dest_lat", transaction.shipment.lat);
        map.put("dest_lng", transaction.shipment.lng);
        APIManager.getRepository(GoogleMapRepo.class).getDirection(map)
                .enqueue(new APICallback<Direction>() {
            @Override
            public void onSuccess(Call<Direction> call, retrofit2.Response<Direction> response) {
                super.onSuccess(call, response);
                tvDistance.setText("Estimasi jarak: " + response.body().routes[0].legs[0].distance
                        .text);
                tvETA.setText("Estimasi sampai: " + response.body().routes[0].legs[0]
                        .durationInTraffic.text);
                if(transaction.status == 0){
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
                    moveMap();


                    LatLng position = new LatLng(transaction.shipment.lat, transaction.shipment.lng);

                    if(animate){
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .flat(true));
                        animateMarker(mMap, marker, listLatLang, true);
                    }

                }
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

    public void confirmTerimaBarang(){
        showDialog("Konfirmasi terima barang");
        APIManager.getRepository(TransactionRepo.class).approve(transaction.id)
                .enqueue(new APICallback<com.project.michael.base.models.Response>() {
                    @Override
                    public void onSuccess(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                        super.onSuccess(call, response);
                        dismissDialog();
                        Intent i = new Intent();
                        i.putExtra(ConstClass.TRANSACTION_EXTRA,GsonUtils.getJsonFromObject
                                (transaction));
                        setResult(TransactionFragment.RESPONSE_TERIMA_BARANG,i);
                        finish();

                    }

                    @Override
                    public void onFailure(Call<com.project.michael.base.models.Response> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan silahkan " +
                                "diulangi lagi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                        super.onError(call, response);
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan silahkan " +
                                "diulangi lagi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showUlasBarang(final int productId, final int vendorId) {
        Map<String, Object> map = new HashMap<>();
        map.put("vendor_id", vendorId);
        map.put("product_id", productId);
        Call<GenericResponse<Review>> checkReview = APIManager.getRepository(ReviewRepo.class)
                .checkReview(map);
        checkReview.enqueue(new APICallback<GenericResponse<Review>>() {
            @Override
            public void onSuccess(Call<GenericResponse<Review>> call,
                                  Response<GenericResponse<Review>> response) {
                super.onSuccess(call, response);
                AddEditReviewDialogFragment addEditReviewDialogFragment =
                        new AddEditReviewDialogFragment();
                addEditReviewDialogFragment.setReview(response.body().data);
                addEditReviewDialogFragment.setProductAndVendorId(productId, vendorId);
                addEditReviewDialogFragment.show(getSupportFragmentManager(),
                        "addEditReviewDialog");
            }

            @Override
            public void onNotFound(Call<GenericResponse<Review>> call,
                                   Response<GenericResponse<Review>> response) {
                super.onNotFound(call, response);
                AddEditReviewDialogFragment addEditReviewDialogFragment =
                        new AddEditReviewDialogFragment();
                addEditReviewDialogFragment.setProductAndVendorId(productId, vendorId);
                addEditReviewDialogFragment.show(getSupportFragmentManager(),
                        "addEditReviewDialog");
            }

            @Override
            public void onFailure(Call<GenericResponse<Review>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_terima_barang:
                confirmTerimaBarang();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(3.637795, 98.687459);
//        mMap.addMarker(new MarkerOptions().position(sydney));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        mMap.moveCamera(yourLocation);
    }

    private void moveMap() {
        if(marker != null) marker.remove();
        LatLng position = new LatLng(transaction.shipment.lat, transaction.shipment.lng);
        marker = mMap.addMarker(new MarkerOptions().position(position));

        if(vendorMarker != null) vendorMarker.remove();
        LatLng vendorPosition = new LatLng(transaction.detail.get(0).vendor.vendor.latitude, transaction.detail.get(0).vendor.vendor.longitude);
        vendorMarker = mMap.addMarker(new MarkerOptions().position(vendorPosition));

        CameraUpdate yourLocation;

        if(latLngBounds != null){
            yourLocation = CameraUpdateFactory.newLatLngBounds(latLngBounds, 100);
        }else{
            LatLng latLng = new LatLng(transaction.shipment.lat, transaction.shipment.lng);
            yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        }

        mMap.moveCamera(yourLocation);
//        setMycurrentPosition(false);

    }

    private void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
