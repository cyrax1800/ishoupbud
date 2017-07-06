package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.view.activities.DetailTransactionActivity;
import com.project.ishoupbud.view.holders.ProductTransactionHolder;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/14/17.
 */

public class ProductTransactionAdapter<Model> extends FastAdapter<Model> {

    DetailTransactionActivity detailTransactionActivity;

    public ProductTransactionAdapter(DetailTransactionActivity detailTransactionActivity) {
        this.detailTransactionActivity = detailTransactionActivity;
    }

    public ProductTransactionAdapter(){

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_product_transcation, parent, false);
        return super.onPostCreateViewHolder(new ProductTransactionHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductTransactionHolder shoppingCartHolder = (ProductTransactionHolder) holder;
        ShoppingCart shoppingCart = (ShoppingCart) mModelList.get(position);

        shoppingCartHolder.tvProductName.setText(shoppingCart.product.name);
        shoppingCartHolder.tvQuantity.setText(String.valueOf(shoppingCart.quantity));
        shoppingCartHolder.tvSummary.setText(Utils.indonesiaFormat(shoppingCart.quantity *
                shoppingCart.product.price));

        Glide
                .with(shoppingCartHolder.ivProduct.getContext())
                .load(shoppingCart.product.pictureUrl.small)
                .centerCrop()
                .crossFade()
                .into(shoppingCartHolder.ivProduct);

        if(detailTransactionActivity == null){
            shoppingCartHolder.btnUlasBarang.setVisibility(View.GONE);
        }else{
            if(detailTransactionActivity.transaction.status == 1){
                shoppingCartHolder.btnUlasBarang.setVisibility(View.VISIBLE);
            }else{
                shoppingCartHolder.btnUlasBarang.setVisibility(View.GONE);
            }
        }
    }
}
