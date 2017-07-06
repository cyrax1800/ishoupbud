package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/10/17.
 */

public class SimpleReviewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_review_name) public TextView tvUserName;
    @BindView(R.id.tv_review_description) public TextView tvReviewDesc;
    @BindView(R.id.rating_bar_review) public RatingBar ratingBar;

    public SimpleReviewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this,itemView);
    }
}
