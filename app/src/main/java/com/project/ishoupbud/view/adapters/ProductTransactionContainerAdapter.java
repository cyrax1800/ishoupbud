package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.view.activities.BayarActivity;
import com.project.ishoupbud.view.holders.ProductTransactionContainerHolder;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.FastAdapter;
import com.project.michael.base.views.helpers.DividerItemDecoration;

/**
 * Created by michael on 6/29/17.
 */

public class ProductTransactionContainerAdapter<Model> extends FastAdapter<Model> {

    public BayarActivity bayarActivity;

    public ProductTransactionContainerAdapter(BayarActivity bayarActivity) {
        this.bayarActivity = bayarActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_product_transaction_container, parent, false);
        final ProductTransactionContainerHolder productTransactionHolder =
                new ProductTransactionContainerHolder(itemView);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(bayarActivity.getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false);

        productTransactionHolder.rvitem
                .addItemDecoration(
                        new DividerItemDecoration(bayarActivity.getApplicationContext(),
                                LinearLayoutManager.VERTICAL)
                );
        productTransactionHolder.rvitem.setLayoutManager(layoutManager);

        return super.onPostCreateViewHolder(productTransactionHolder, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProductTransactionContainerHolder shoppingCartHolder =
                (ProductTransactionContainerHolder) holder;
        ShoppingCartContainer shoppingCartContainer =
                (ShoppingCartContainer) mModelList.get(position);
        shoppingCartHolder.shoppingCartAdapter.setNew(shoppingCartContainer.item);
        shoppingCartHolder.rvitem.setAdapter(shoppingCartHolder.shoppingCartAdapter);
        shoppingCartHolder.tvVendor.setText(shoppingCartContainer.vendor.name);
        shoppingCartHolder.tvBiayaKirim.setText(Utils.indonesiaFormat(shoppingCartContainer
                .shippingPrice));
        shoppingCartHolder.tvEstimasiWaktu.setText(shoppingCartContainer.duration);
        shoppingCartHolder.tvJarak.setText(shoppingCartContainer.distance);
        if (shoppingCartContainer.isFetching) {
            shoppingCartHolder.progressBar.setVisibility(View.VISIBLE);
            shoppingCartHolder.llDetailShipment.setVisibility(View.GONE);
        } else {
            shoppingCartHolder.progressBar.setVisibility(View.GONE);
            shoppingCartHolder.llDetailShipment.setVisibility(View.VISIBLE);
        }
    }

}
