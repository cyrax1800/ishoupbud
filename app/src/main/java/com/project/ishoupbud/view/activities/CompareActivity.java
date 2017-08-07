package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Compare;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.api.response.ProductAllReviewResponse;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ReviewAdapter;
import com.project.ishoupbud.view.adapters.SimpleReviewAdapter;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.helpers.DividerItemDecoration;
import com.project.michael.base.views.listeners.OnSwipeTouchListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

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
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    SimpleReviewAdapter<Review> reviewAdapter;

    @BindView(R.id.ll_comparer) LinearLayout llComparer;
    @BindView(R.id.iv_compare_product) ImageView ivCompareProduct;
    @BindView(R.id.tv_compare_product_title) TextView tvCompareProductName;
    @BindView(R.id.tv_compare_product_price) TextView tvCompareProductPrice;
    @BindView(R.id.rating_bar_compare_product) RatingBar rbCompareProduct;
    @BindView(R.id.tv_rate_value_compare_product) TextView tvCompareRatingValue;
    @BindView(R.id.tv_detail_compare_product) TextView tvCompareDetailProduct;
    @BindView(R.id.rv_compare_review) RecyclerView rvCompareReview;
    @BindView(R.id.compare_progress_bar) ProgressBar compareProgressBar;
    @BindView(R.id.ib_left) ImageButton ibLeft;
    @BindView(R.id.ib_right) ImageButton ibRight;
    @BindView(R.id.tv_total_item) TextView tvPage;
    SimpleReviewAdapter<Review> compareReviewAdapter;

    Product source;
    List<Product> target;

    int currentIndex;

    Call<ProductAllReviewResponse> reviewCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        ButterKnife.bind(this);

        toolbar.setTitle("Compare");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Compare compare = GsonUtils.getObjectFromJson(getIntent().getStringExtra(ConstClass
                .COMPARE_EXTRA), Compare.class);

        source = compare.source;
        target = compare.products;

        llComparer.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.d(TAG, "onSwipeLeft: ");
                if(currentIndex >= target.size() - 1) return;
                currentIndex++;
                setTargetProduct(target.get(currentIndex));

                tvPage.setText((currentIndex + 1) + "/" + target.size());
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Log.d(TAG, "onSwipeRight: ");
                if(currentIndex <= 0) return;
                currentIndex--;
                setTargetProduct(target.get(currentIndex));

                tvPage.setText((currentIndex + 1) + "/" + target.size());
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                Log.d(TAG, "onSwipeBottom: ");
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                Log.d(TAG, "onSwipeTop: ");
            }
        });

        reviewAdapter = new SimpleReviewAdapter<>();
        compareReviewAdapter = new SimpleReviewAdapter<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);

        rvReview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rvReview.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        rvCompareReview.setLayoutManager(layoutManager);
        rvCompareReview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        rvReview.setAdapter(reviewAdapter);
        rvCompareReview.setAdapter(compareReviewAdapter);

        currentIndex = 0;
        setSourceProduct();
        setTargetProduct(target.get(currentIndex));

        tvPage.setText((currentIndex + 1) + "/" + target.size());

        ibLeft.setOnClickListener(this);
        ibRight.setOnClickListener(this);
    }

    public void setSourceProduct(){
        Glide
                .with(this)
                .load(source.pictureUrl.medium)
                .placeholder(R.drawable.comingsoon)
                .fitCenter()
                .crossFade()
                .into(ivProduct);
        tvProductName.setText(source.name);
        tvProductPrice.setText(Utils.indonesiaFormat(source.price));
        rbProduct.setRating((float) source.totalRating);
        tvRatingValue.setText(String.valueOf(source.totalRating));
        tvDetailProduct.setMinLines(0);
        tvDetailProduct.setText(source.description);

        progressBar.setVisibility(View.VISIBLE);

        getSourceReview();
    }

    public void setTargetProduct(Product productTarget){
        Glide
                .with(this)
                .load(productTarget.pictureUrl.medium)
                .placeholder(R.drawable.comingsoon)
                .fitCenter()
                .crossFade()
                .into(ivCompareProduct);
        tvCompareProductName.setText(productTarget.name);
        tvCompareProductPrice.setText(Utils.indonesiaFormat(productTarget.price));
        rbCompareProduct.setRating((float) productTarget.totalRating);
        tvCompareRatingValue.setText(String.valueOf(productTarget.totalRating));
        tvCompareDetailProduct.setMinLines(0);
        tvCompareDetailProduct.setText(productTarget.description);
        tvCompareDetailProduct.post(new Runnable() {
            @Override
            public void run() {
                int maxLines = Math.max(tvCompareDetailProduct.getLineCount(), tvDetailProduct.getLineCount());
                tvCompareDetailProduct.setMinLines(maxLines);
                tvDetailProduct.setMinLines(maxLines);
            }
        });


        compareProgressBar.setVisibility(View.VISIBLE);
        getTargetReview(productTarget);

    }

    public void getSourceReview(){
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", source.id);
        Call<ProductAllReviewResponse> getReview = APIManager.getRepository(ReviewRepo.class).getReview(map);
        getReview.enqueue(new APICallback<ProductAllReviewResponse>() {
            @Override
            public void onSuccess(Call<ProductAllReviewResponse> call, Response<ProductAllReviewResponse> response) {
                super.onSuccess(call, response);
                reviewAdapter.setNew(response.body().reviews);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductAllReviewResponse> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void getTargetReview(Product productTarget){
        compareProgressBar.setVisibility(View.VISIBLE);
        compareReviewAdapter.clear();
        if(reviewCall != null  && reviewCall.isExecuted()){
            reviewCall.cancel();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", productTarget.id);
        Call<ProductAllReviewResponse> getReview = APIManager.getRepository(ReviewRepo.class).getReview(map);
        getReview.enqueue(new APICallback<ProductAllReviewResponse>() {
            @Override
            public void onSuccess(Call<ProductAllReviewResponse> call, Response<ProductAllReviewResponse> response) {
                super.onSuccess(call, response);
                compareReviewAdapter.setNew(response.body().reviews);
                compareProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductAllReviewResponse> call, Throwable t) {
                super.onFailure(call, t);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_left:
                if(currentIndex <= 0) return;
                currentIndex--;
                setTargetProduct(target.get(currentIndex));

                tvPage.setText((currentIndex + 1) + "/" + target.size());
                break;
            case R.id.ib_right:
                if(currentIndex >= target.size() - 1) return;
                currentIndex++;
                setTargetProduct(target.get(currentIndex));

                tvPage.setText((currentIndex + 1) + "/" + target.size());
                break;
        }
    }
}
