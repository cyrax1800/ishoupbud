package com.project.ishoupbud.view.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StringLoader;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductPagerAdapter;
import com.project.ishoupbud.view.adapters.VendorAdapter;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/9/17.
 */

public class ProductActivity extends BaseActivity {

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    TextView toolbar_title;
    @BindView(R.id.iv_product) ImageView ivProduct;

    @BindView(R.id.tv_product_name) TextView tvProductName;
    @BindView(R.id.tv_rating_summary) TextView tvRatingSummary;
    @BindView(R.id.tv_total_rater) TextView tvTotalRater;
    @BindView(R.id.tv_sentiment) TextView tvSentiment;
    @BindView(R.id.rating_bar_summary) RatingBar ratingBar;
    @BindView(R.id.rv_vendor) RecyclerView rvVendor;
    @BindView(R.id.btn_add_to_cart) Button btnAddToCart;
    @BindView(R.id.btn_compare) Button btnCompare;

    @BindView(R.id.btn_plus_stepper) ImageButton ibtnPlusStepper;
    @BindView(R.id.btn_minus_stepper) ImageButton ibtnMinusStepper;
    @BindView(R.id.et_stepper_count) EditText etStepperCount;

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

    Product product;
    int productQuantity;

    VendorAdapter<Vendor> vendorAdapter;
    ProductPagerAdapter productPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ButterKnife.bind(this);

        productQuantity = 1;
        product = GsonUtils.getObjectFromJson(getIntent().getStringExtra(ConstClass.PRODUCT_EXTRA),Product.class);

        collapsingToolbarLayout.setTitleEnabled(false);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(product.name);
        toolbar_title.setAlpha(0.0f);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar_title.animate().alpha(1.0f).setDuration(250);
                    isShow = true;
                } else if(isShow) {
                    toolbar_title.animate().alpha(0.0f).setDuration(250);
                    isShow = false;
                }
            }
        });

        Glide
                .with(this)
//                .load(product.picUrl)
                .load("http://kingofwallpapers.com/aqua/aqua-001.jpg")
                .centerCrop()
                .crossFade()
                .into(ivProduct);

        tvProductName.setText(product.name);
        tvRatingSummary.setText(String.valueOf(product.ratingSummary));
        tvTotalRater.setText("(10 Reviews)");
        tvSentiment.setText("OverAll: Very Positif");
        ratingBar.setRating((float)product.ratingSummary);

        vendorAdapter = new VendorAdapter<>();
        rvVendor.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvVendor.setAdapter(vendorAdapter);

        btnAddToCart.setOnClickListener(this);
        btnCompare.setOnClickListener(this);

        ibtnMinusStepper.setOnClickListener(this);
        ibtnPlusStepper.setOnClickListener(this);
        etStepperCount.setText(String.valueOf(productQuantity));

        productPagerAdapter = new ProductPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(productPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                } else if (position == 1) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product,menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_to_cart:
                break;
            case R.id.btn_compare:
                break;
            case R.id.btn_plus_stepper:
                break;
            case R.id.btn_minus_stepper:
                break;
        }
    }
}
