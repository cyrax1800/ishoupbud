package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.view.holders.ProductTransactionHolder;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/14/17.
 */

public class ProductTransactionAdapter<Model> extends FastAdapter<Model> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_transcation,parent,false);
        return super.onPostCreateViewHolder(new ProductTransactionHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductTransactionHolder shoppingCartHolder = (ProductTransactionHolder)holder;
        ShoppingCart shoppingCart = (ShoppingCart)mModelList.get(position);

        shoppingCartHolder.tvProductName.setText(shoppingCart.product.name);
        shoppingCartHolder.tvPrice.setText("Rp. " + shoppingCart.vendor.price);
        shoppingCartHolder.tvVendor.setText(shoppingCart.vendor.name);
        shoppingCartHolder.tvQuantity.setText("Quantity: " + shoppingCart.quantity);
        shoppingCartHolder.tvSummary.setText("Summary: Rp. " + shoppingCart.quantity * shoppingCart.vendor.price);

        Glide
                .with(shoppingCartHolder.ivProduct.getContext())
//                .load(product.picUrl)
                .load("http://kingofwallpapers.com/aqua/aqua-001.jpg")
                .centerCrop()
                .crossFade()
                .into(shoppingCartHolder.ivProduct);

    }
}
