package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/9/17.
 */

public class TransactionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_transaction) public ImageView ivType;
    @BindView(R.id.tv_no_transaction) public TextView tvNoTransaction;
    @BindView(R.id.tv_transcation_date) public TextView tvDate;
    @BindView(R.id.tv_transaction_type) public TextView tvType;
    @BindView(R.id.tv_transaction_price) public TextView tvPrice;

    public TransactionHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this,itemView);
    }
}
