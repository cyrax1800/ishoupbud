package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.view.holders.ProductHolder;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/9/17.
 */

public class ProductAdapter<Model> extends FastAdapter<Model> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_product,parent,false);
        return super.onPostCreateViewHolder(new ProductHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductHolder productHolder = (ProductHolder)holder;
        Product product = (Product)mModelList.get(position);

        productHolder.tvTitle.setText(product.name);
        productHolder.tvPrice.setText("Rp. " + product.price);
        productHolder.tvRating.setText("(" + product.ratingSummary + ")");
        productHolder.ratingBar.setRating((float)product.ratingSummary);

        Glide
                .with(productHolder.ivProductImage.getContext())
//                .load(product.picUrl)
                .load("http://kingofwallpapers.com/aqua/aqua-001.jpg")
                .centerCrop()
                .crossFade()
                .into(productHolder.ivProductImage);

    }
}