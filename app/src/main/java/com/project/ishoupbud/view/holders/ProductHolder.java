package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/9/17.
 */

public class ProductHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_card_product_image) public ImageView ivProductImage;
    @BindView(R.id.tv_card_product_title) public TextView tvTitle;
    @BindView(R.id.tv_card_product_price) public TextView tvPrice;
    @BindView(R.id.rating_bar_card_product) public RatingBar ratingBar;
    @BindView(R.id.tv_rate_value_card_product) public TextView tvRating;

    public ProductHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
