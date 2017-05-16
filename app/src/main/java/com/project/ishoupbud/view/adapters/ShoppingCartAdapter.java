package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.view.StepperView;
import com.project.ishoupbud.view.holders.ShoppingCartHolder;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/13/17.
 */

public class ShoppingCartAdapter<Model> extends FastAdapter<Model> {

    public StepperView.OnValueChangeListener onValueChangeListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_cart,parent,false);
        return super.onPostCreateViewHolder(new ShoppingCartHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShoppingCartHolder shoppingCartHolder = (ShoppingCartHolder)holder;
        ShoppingCart shoppingCart = (ShoppingCart)mModelList.get(position);

        shoppingCartHolder.tvProductName.setText(shoppingCart.product.name);
        shoppingCartHolder.tvPrice.setText(Utils.indonesiaFormat(shoppingCart.product.price));
        shoppingCartHolder.tvVendor.setText(shoppingCart.product.vendor.name);
        shoppingCartHolder.stepperView.setValue(shoppingCart.quantity);
        shoppingCartHolder.stepperView.setOnValueChangeListener(onValueChangeListener);

        Glide
                .with(shoppingCartHolder.ivProduct.getContext())
                .load(shoppingCart.product.pictureUrl.small)
                .centerCrop()
                .crossFade()
                .into(shoppingCartHolder.ivProduct);

    }
}
