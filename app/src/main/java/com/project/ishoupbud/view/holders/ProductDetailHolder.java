package com.project.ishoupbud.view.holders;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.view.StepperView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 6/26/17.
 */

public class ProductDetailHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_product_name)
    public TextView tvProductName;
    @BindView(R.id.tv_rating_summary)
    public TextView tvRatingSummary;
    @BindView(R.id.tv_total_rater)
    public TextView tvTotalRater;
    @BindView(R.id.tv_sentiment)
    public TextView tvSentiment;
    @BindView(R.id.rating_bar_summary)
    public RatingBar ratingBar;
    @BindView(R.id.rv_vendor)
    public RecyclerView rvVendor;
    @BindView(R.id.btn_add_to_cart)
    public Button btnAddToCart;
    @BindView(R.id.btn_compare)
    public Button btnCompare;

    @BindView(R.id.stepper)
    public StepperView stepperView;

    public ProductDetailHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        rvVendor.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}
