package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.view.holders.ProductHolder;
import com.project.ishoupbud.view.holders.ReviewHolder;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.EndlessScrollAdapter;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/9/17.
 */

public class ProductPagingAdapter<Model> extends EndlessScrollAdapter<Model> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_product,parent,false);
            return super.onPostCreateViewHolder(new ProductHolder(itemView), viewType);
        }
        return super.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProductHolder){
            ProductHolder productHolder = (ProductHolder)holder;
            Product product = (Product)mModelList.get(position);

            productHolder.tvTitle.setText(product.name);
            productHolder.tvPrice.setText(Utils.indonesiaFormat(product.price));
//        productHolder.tvRating.setText("(" + product.totalRating + ")");

            productHolder.tvRating.setText(Utils.reviewFormat(product.totalRating));
            productHolder.ratingBar.setRating((float)product.totalRating);

            Glide
                    .with(productHolder.ivProductImage.getContext())
                    .load(product.pictureUrl.small)
                    .placeholder(R.drawable.comingsoon)
                    .fitCenter()
                    .crossFade()
                    .into(productHolder.ivProductImage);
        }else{
            super.onBindViewHolder(holder,position);
        }

    }
}
