package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductTransactionAdapter;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/14/17.
 */

public class DetailTransactionActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_no_transaction) TextView tvNoTransaction;
    @BindView(R.id.tv_status) TextView tvStatus;
    @BindView(R.id.tv_total_price) TextView tvPrice;
    @BindView(R.id.tv_estimate_time) TextView tvETA;
    @BindView(R.id.tv_distance) TextView tvDistance;
    @BindView(R.id.rv_product) RecyclerView rvProduct;

    private GoogleMap mMap;

    ProductTransactionAdapter<ShoppingCart> productTransactionAdapter;
    Transaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        ButterKnife.bind(this);

        toolbar.setTitle("Detail Transaction");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        transaction = GsonUtils.getObjectFromJson(getIntent().getStringExtra(ConstClass.TRANSACTION_EXTRA),Transaction.class);

        productTransactionAdapter = new ProductTransactionAdapter<>();
        productTransactionAdapter.setNew(transaction.detail);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        rvProduct.addItemDecoration(new InsetDividerItemDecoration(this));
        rvProduct.setLayoutManager(layoutManager);
        rvProduct.setAdapter(productTransactionAdapter);

        tvNoTransaction.setText("No. Transaction: " + transaction.id);
        tvPrice.setText("Total Price: " + Utils.indonesiaFormat(transaction.nominal));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(3.637795, 98.687459);
        mMap.addMarker(new MarkerOptions().position(sydney));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        mMap.moveCamera(yourLocation);
    }
}
