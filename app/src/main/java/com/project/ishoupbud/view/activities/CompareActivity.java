package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/13/17.
 */

public class CompareActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.iv_product) ImageView ivProduct;
    @BindView(R.id.tv_card_product_title) TextView tvProductName;
    @BindView(R.id.tv_card_product_price) TextView tvProductPrice;
    @BindView(R.id.rating_bar_card_product) RatingBar rbProduct;
    @BindView(R.id.tv_rate_value_card_product) TextView tvRatingValue;
    @BindView(R.id.tv_detail_product) TextView tvDetailProduct;
    @BindView(R.id.rv_review) RecyclerView rvReview;

    @BindView(R.id.iv_compare_product) ImageView ivCompareProduct;
    @BindView(R.id.tv_compare_product_title) TextView tvCompareProductName;
    @BindView(R.id.tv_compare_product_price) TextView tvCompareProductPrice;
    @BindView(R.id.rating_bar_compare_product) RatingBar rbCompareProduct;
    @BindView(R.id.tv_rate_value_compare_product) TextView tvCompareRatingValue;
    @BindView(R.id.tv_detail_compare_product) TextView tvCompareDetailProduct;
    @BindView(R.id.rv_compare_review) RecyclerView rvCompareReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        toolbar.setTitle("Compare");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
