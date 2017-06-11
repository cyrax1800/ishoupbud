package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.view.holders.TransactionHolder;
import com.project.michael.base.utils.DateUtils;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/9/17.
 */

public class TransactionAdapter<Model> extends FastAdapter<Model> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent,false);
        return super.onPostCreateViewHolder(new TransactionHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TransactionHolder transactionHolder = (TransactionHolder) holder;
        Transaction transaction = (Transaction)mModelList.get(position);

        transactionHolder.tvNoTransaction.setText(String.valueOf(transaction.id));
        transactionHolder.tvPrice.setText("Rp. " + transaction.nominal);
        transactionHolder.tvDate.setText(DateUtils.getDate(transaction.date.getTime()));
        if(transaction.type.equals("User"))
            transactionHolder.tvType.setText(transaction.vendor);
        else
            transactionHolder.tvType.setText("Top up");

        Glide
                .with(transactionHolder.ivType.getContext())
//                .load(product.picUrl)
                .load("http://kingofwallpapers.com/aqua/aqua-001.jpg")
                .centerCrop()
                .crossFade()
                .into(transactionHolder.ivType);
    }
}
