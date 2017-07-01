package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/14/17.
 */

public class ProductTransactionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_product) public ImageView ivProduct;
    @BindView(R.id.tv_product_name) public TextView tvProductName;
    @BindView(R.id.tv_quantity) public TextView tvQuantity;
    @BindView(R.id.tv_summary) public TextView tvSummary;

    public ProductTransactionHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
