package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductTransactionAdapter;
import com.project.ishoupbud.view.dialog.AddEditReviewDialogFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.listeners.ClickEventListener;

import java.util.HashMap;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        ButterKnife.bind(this);

        toolbar.setTitle("Detail Transaction");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        transaction = GsonUtils.getObjectFromJson(getIntent().getStringExtra(
                ConstClass.TRANSACTION_EXTRA), Transaction.class);
        if (transaction.status == 0) {
            btnBarangTerima.setVisibility(View.VISIBLE);
        } else if (transaction.status == 1) {
            btnBarangTerima.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                    nestedScrollView.getLayoutParams();
            marginLayoutParams.setMargins(0, 0, 0, 0);
            nestedScrollView.setLayoutParams(marginLayoutParams);
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
        mMap.addMarker(new MarkerOptions().position(sydney));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        mMap.moveCamera(yourLocation);
    }
}
