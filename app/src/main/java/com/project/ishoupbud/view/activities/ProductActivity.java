package com.project.ishoupbud.view.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.Uber.UberAPI;
import com.project.ishoupbud.api.Uber.model.UberPrices;
import com.project.ishoupbud.api.Uber.repositories.UberRepo;
import com.project.ishoupbud.api.model.Compare;
import com.project.ishoupbud.api.model.FetchingShipmentDataHelper;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.ProductVendors;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.model.WishList;
import com.project.ishoupbud.api.model.map.Direction;
import com.project.ishoupbud.api.model.pusher.ProductPusher;
import com.project.ishoupbud.api.repositories.GoogleMapRepo;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.api.repositories.WishlistRepo;
import com.project.ishoupbud.manager.GoogleAPIManager;
import com.project.ishoupbud.manager.PusherManager;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductDetailAdapter;
import com.project.ishoupbud.view.adapters.ProductPagerAdapter;
import com.project.ishoupbud.view.adapters.VendorAdapter;
import com.project.ishoupbud.view.holders.ProductDetailHolder;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseActivity;
import com.pusher.client.channel.ChannelEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/9/17.
 */

public class ProductActivity extends BaseActivity implements
        FetchingShipmentDataHelper.onFetchingListener<ProductVendors> {

    public int productQuantity;
    public VendorAdapter<ProductVendors> vendorAdapter;
    public ProductPagerAdapter productPagerAdapter;
    public int totalItemInFetching = 5;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView toolbar_title;
    @BindView(R.id.iv_product)
    ImageView ivProduct;
    @BindView(R.id.rv_product_detail)
    RecyclerView rvProductDetail;
    ProductDetailAdapter productDetailAdapter;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    Menu menu;
    Product product;
    boolean isInWishlist = false;
    List<FetchingShipmentDataHelper<ProductVendors>> fetchingShipmentDataHelpers;
    double longitude, latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ButterKnife.bind(this);

        productQuantity = 1;
        product = GsonUtils.getObjectFromJson(getIntent().getStringExtra(ConstClass
                .PRODUCT_EXTRA), Product.class);

        collapsingToolbarLayout.setTitleEnabled(false);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(product.name);
        toolbar_title.setAlpha(0.0f);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestProduct();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                Log.d(TAG, "onOffsetChanged: " + scrollRange + " " + verticalOffset + " " +
// toolbar.getHeight());
                if (!swipeRefreshLayout.isRefreshing()) {
                    if (verticalOffset > -10) {
                        swipeRefreshLayout.setEnabled(true);
                    } else {
                        swipeRefreshLayout.setEnabled(false);
                    }
                }
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset - toolbar.getHeight() <= 0) {
                    if (isShow) return;
                    collapsingToolbarLayout.setScrimVisibleHeightTrigger(toolbar.getHeight() + 1);
                    toolbar_title.animate().alpha(1.0f).setDuration(250);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setScrimVisibleHeightTrigger(toolbar.getHeight());
                    toolbar_title.animate().alpha(0.0f).setDuration(200);
                    isShow = false;
                }
            }
        });


        Glide
                .with(this)
                .load(product.pictureUrl.medium)
                .fitCenter()
                .crossFade()
                .into(ivProduct);

        fetchingShipmentDataHelpers = new ArrayList<>();
        for (int i = 0; i < product.vendors.size(); i++) {
            fetchingShipmentDataHelpers.add(new FetchingShipmentDataHelper<ProductVendors>(i, this));
        }

        vendorAdapter = new VendorAdapter<>();
//        vendorAdapter.setNew(product.vendors);

        productDetailAdapter = new ProductDetailAdapter(product, this);
        rvProductDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false));
        rvProductDetail.setAdapter(productDetailAdapter);
//
        productPagerAdapter = new ProductPagerAdapter(getSupportFragmentManager());

        initProgressDialog("Adding to cart...");

        PusherManager.getInstance().listenToProduct(product.id, new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String s) {

            }

            @Override
            public void onEvent(String channel, String event, String data) {
                Log.d(TAG, "onEvent: Product " + data);
                ProductPusher productPusher = GsonUtils.getObjectFromJson(data,
                        ProductPusher.class);
                product = productPusher.review.product;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        });

        LatLng latLng = GoogleAPIManager.getGoogleApi().getCurrentPosition(this);
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public void sortVendorByPrice() {
        totalItemInFetching = product.vendors.size();
        for (int i = 0; i < product.vendors.size(); i++) {
            fetchingShipmentDataHelpers.get(i).startFetch(product.vendors.get(i));
            getPath(i);
            getEstimatePrice(i);
        }
    }

    public void sortVendor() {
        Collections.sort(product.vendors, new Comparator<ProductVendors>() {
            @Override
            public int compare(ProductVendors o1, ProductVendors o2) {
                if ((o1.price + o1.shippingPrice) < (o2.price + o2.shippingPrice)) {
                    return -1;
                }else if((o1.price + o1.shippingPrice) > (o2.price + o2.shippingPrice)) {
                    return 1;
                }
                return 0;
            }
        });
        if(product.vendors.size() > 5){
            product.vendors.subList(5, product.vendors.size() - 1).clear();
        }
        vendorAdapter.setNew(product.vendors);
    }

    public void getPath(final int position) {
        final ProductVendors data = product.vendors.get(position);
        HashMap<String, Object> map = new HashMap<>();
        map.put("origin_lat", data.vendor.latitude);
        map.put("origin_lng", data.vendor.longitude);
        map.put("dest_lat", latitude);
        map.put("dest_lng", longitude);
        final FetchingShipmentDataHelper<ProductVendors> fetcher = fetchingShipmentDataHelpers.get(position);
        if (fetcher.callGetTime != null && fetcher.callGetTime.isExecuted()) {
            fetcher.callGetTime.cancel();
        }
        fetcher.callGetTime = APIManager.getRepository(GoogleMapRepo.class)
                .getDirection(map);
        fetcher.callGetTime.enqueue(new APICallback<Direction>() {
            @Override
            public void onSuccess(Call<Direction> call, retrofit2.Response<Direction> response) {
                super.onSuccess(call, response);
                fetcher.data.distance = response.body().routes[0].legs[0].distance.text;
                fetcher.doneTime();
//                product.vendors.set(position, fetcher.data);
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

    public void getEstimatePrice(final int position) {
        final ProductVendors data = product.vendors.get(position);
        HashMap<String, Object> map = new HashMap<>();
        map.put("start_latitude", data.vendor.latitude);
        map.put("start_longitude", data.vendor.longitude);
        map.put("end_latitude", latitude);
        map.put("end_longitude", longitude);
        final FetchingShipmentDataHelper<ProductVendors> fetcher = fetchingShipmentDataHelpers.get(position);
        if (fetcher.callGetPrice != null && fetcher.callGetPrice.isExecuted()) {
            fetcher.callGetPrice.cancel();
        }
        fetcher.callGetPrice = UberAPI.getRepository(UberRepo.class).getEstimatePrice(map);
        fetcher.callGetPrice.enqueue(new APICallback<UberPrices>() {
            @Override
            public void onSuccess(Call<UberPrices> call, retrofit2.Response<UberPrices> response) {
                super.onSuccess(call, response);
                UberPrices uberData = response.body();
                int priceValue = Integer.MAX_VALUE;
                for (int i = 0; i < uberData.prices.size(); i++) {
                    priceValue = Math.min(uberData.prices.get(0).lowEstimate, priceValue);
                }
                fetcher.data.shippingPrice = priceValue;
                fetcher.donePrice();
//                product.vendors.set(position, fetcher.data);
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

    public void requestProduct() {
        progressDialog.setCancelable(false);
        productPagerAdapter.productReviewFragment.requestReview();
        Call<GenericResponse<Product>> getProduct = APIManager.getRepository(ProductRepo.class)
                .getProductById(product.id);
        getProduct.enqueue(new APICallback<GenericResponse<Product>>() {
            @Override
            public void onSuccess(Call<GenericResponse<Product>> call,
                                  Response<GenericResponse<Product>> response) {
                super.onSuccess(call, response);
                product = response.body().data;
                updateView();
                swipeRefreshLayout.setRefreshing(false);
                sortVendorByPrice();
            }

            @Override
            public void onFailure(Call<GenericResponse<Product>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void updateView() {
        productDetailAdapter.product = product;
        productDetailAdapter.notifyItemChanged(0);

        Glide
                .with(this)
                .load(product.pictureUrl.medium)
                .placeholder(R.drawable.comingsoon)
                .fitCenter()
                .crossFade()
                .into(ivProduct);
        productPagerAdapter.productDetailFragment.updateDetail(product.description);
        productPagerAdapter.productStatisticFragment.getStatistic();
        List<Vendor> vendors = new ArrayList<>();
        for (int i = 0; i < product.vendors.size(); i++) {
            vendors.add(product.vendors.get(i).vendor);
        }
        productPagerAdapter.productReviewFragment.setVendor(vendors);
        toolbar_title.setText(product.name);
    }

    public String calculateSummary() {
        String text;
        if (product.productSummary == null) {
            text = "OverAll: Netral";
        } else {
            double maxMean = Math.max(product.productSummary.mean.pos, product.productSummary
                    .mean.neg);
            maxMean = Math.max(maxMean, product.productSummary.mean.neu);
            boolean isPos = maxMean == product.productSummary.mean.pos;
            boolean isNeu = maxMean == product.productSummary.mean.neu;
            boolean isNeg = maxMean == product.productSummary.mean.neg;

            double totalReview = product.productSummary.count.pos + product.productSummary.count
                    .neu + product.productSummary.count.neg;
            double posPercent = product.productSummary.count.pos / totalReview;
            double neuPercent = product.productSummary.count.neu / totalReview;
            double negPercent = product.productSummary.count.neg / totalReview;

            if (isPos && posPercent >= 0.8) {
                text = "Keseluruhan: Luar biasa positif";
            } else if (isPos && posPercent >= 0.65) {
                text = "Keseluruhan: Sangat positif";
            } else if (isPos || isNeu && posPercent >= 0.5) {
                text = "Keseluruhan: positif";
            } else if (isNeg && isNeu && negPercent >= 0.8) {
                text = "Keseluruhan: negatif";
            } else if (isNeg && negPercent >= 0.65) {
                text = "OverAll: Sangat Negatif";
            } else if (isNeg || negPercent >= 0.5) {
                text = "OverAll: Luar biasa negatif";
            } else {
                text = "OverAll: Netral";
            }
        }
        return text;
    }

    public void addToWishList() {
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", product.id);
        Call<GenericResponse<WishList>> addWishlist = APIManager.getRepository(WishlistRepo
                .class).addWishlist(map);
        addWishlist.enqueue(new APICallback<GenericResponse<WishList>>() {
            @Override
            public void onSuccess(Call<GenericResponse<WishList>> call,
                                  Response<GenericResponse<WishList>> response) {
                super.onSuccess(call, response);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite));
                Toast.makeText(getApplicationContext(), "Product successfully added to wishlist",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Call<GenericResponse<WishList>> call,
                                Response<GenericResponse<WishList>> response) {
                super.onError(call, response);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border));
                Toast.makeText(getApplicationContext(), "Product failed added to wishlist", Toast
                        .LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<GenericResponse<WishList>> call, Throwable t) {
                super.onFailure(call, t);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border));
                Toast.makeText(getApplicationContext(), "Something Wrong, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeFromWishList() {
        Call<com.project.michael.base.models.Response> removeWishlist = APIManager.getRepository
                (WishlistRepo.class).deleteWishlist(product.id);
        removeWishlist.enqueue(new APICallback<com.project.michael.base.models.Response>() {
            @Override
            public void onSuccess(Call<com.project.michael.base.models.Response> call,
                                  Response<com.project.michael.base.models.Response> response) {
                super.onSuccess(call, response);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border));
                Toast.makeText(getApplicationContext(), "Product successfully removed to " +
                        "wishlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoContent(Call<com.project.michael.base.models.Response> call,
                                    Response<com.project.michael.base.models.Response> response) {
                super.onNoContent(call, response);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite));
                Toast.makeText(getApplicationContext(), "Product successfully removed to " +
                        "wishlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<com.project.michael.base.models.Response> call, Throwable
                    t) {
                super.onFailure(call, t);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite));
                Toast.makeText(getApplicationContext(), "Something Wrong, please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addItemToCart() {
        if (vendorAdapter.getCheckedIdx() == -1) {
            Toast.makeText(getApplicationContext(), "No vendor selected", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        int value = ((ProductDetailHolder) rvProductDetail.getChildViewHolder(rvProductDetail
                .getChildAt(0))).stepperView.getValue();
        if (value == 0) {
            Toast.makeText(getApplicationContext(), "Quantity can't be zero", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        showDialog("Adding to cart..");
        HashMap<String, Object> map = new HashMap<>();
//        map.put("product_id", product.id);
        map.put("product_vendor_id", vendorAdapter.getItemAt(vendorAdapter.getCheckedIdx()).id);
        map.put("quantity", Integer.valueOf(value));
        Call<ShoppingCart> addItemToCart = APIManager.getRepository(ShoppingCartRepo.class)
                .addCart(map);
        addItemToCart.enqueue(new APICallback<ShoppingCart>() {
            @Override
            public void onCreated(Call<ShoppingCart> call, Response<ShoppingCart> response) {
                super.onCreated(call, response);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Success Added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Call<ShoppingCart> call, Response<ShoppingCart> response) {
                super.onError(call, response);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Harus dipesan melalui vendor yang sama " +
                        "yang berada pada keranjang belanja saat ini", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ShoppingCart> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
            }
        });
    }

    public void compare() {
        showDialog("Get product for compare...");
        Call<Compare> getCompareProduct = APIManager.getRepository(ProductRepo.class)
                .compareProduct(product.id);
        getCompareProduct.enqueue(new APICallback<Compare>() {
            @Override
            public void onSuccess(Call<Compare> call, Response<Compare> response) {
                super.onSuccess(call, response);
                Intent compareIntent = new Intent(ProductActivity.this, CompareActivity.class);
                compareIntent.putExtra(ConstClass.COMPARE_EXTRA,
                        GsonUtils.getJsonFromObject(response.body(), Compare.class));
                startActivity(compareIntent);
                dismissDialog();
            }

            @Override
            public void onFailure(Call<Compare> call, Throwable t) {
                super.onFailure(call, t);
                dismissDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product, menu);
        this.menu = menu;
        isInWishlist = product.liked;
        if (product.liked) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite));
        } else {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border));
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_shopping_cart) {
            Intent i = new Intent(this, ShoppingCartActivity.class);
            startActivity(i);
        } else if (item.getItemId() == R.id.action_wishlist) {
            if (!isInWishlist) {
                item.setIcon(getResources().getDrawable(R.drawable.ic_favorite));
                isInWishlist = true;
                addToWishList();
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border));
                isInWishlist = false;
                removeFromWishList();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_to_cart:
                addItemToCart();
                break;
            case R.id.btn_compare:
                compare();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        PusherManager.getInstance().disconnectListenToProduct();
    }

    @Override
    public void onCompleteFetch(int idx, ProductVendors data) {
        totalItemInFetching--;
//        if(fetchingShipmentDataHelpers.get(idx).status){
            product.vendors.set(idx, data);
//        }

        if (totalItemInFetching == 0) {
            sortVendor();
            dismissDialog();
        }
    }
}
