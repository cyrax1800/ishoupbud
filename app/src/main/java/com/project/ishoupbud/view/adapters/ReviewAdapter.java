package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.view.holders.ReviewHolder;
import com.project.michael.base.utils.DateUtils;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.views.adapters.EndlessScrollAdapter;

/**
 * Created by michael on 4/11/17.
 */

public class ReviewAdapter<Model> extends EndlessScrollAdapter<Model> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,parent,false);
            return super.onPostCreateViewHolder(new ReviewHolder(itemView), viewType);
        }
        return super.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ReviewHolder){
            ReviewHolder reviewHolder = (ReviewHolder) holder;
            Review review = (Review) mModelList.get(position);

            Glide
                    .with(reviewHolder.ivUserProfilePic.getContext())
                    .load(review.user.getSmallImage())
                    .placeholder(TextImageCircleHelper.getInstance().getImage(review.user.name))
                    .centerCrop()
                    .crossFade()
                    .into(reviewHolder.ivUserProfilePic);

            reviewHolder.tvUserName.setText(review.user.name);
//            reviewHolder.tvReviewDate.setText(DateUtils.getDate(review.date.getTime()));
            reviewHolder.tvReviewDesc.setText(review.description);
            reviewHolder.ratingBar.setRating((float)review.rating);
            if(review.sentiment.neu > review.sentiment.pos && review.sentiment.neu > review.sentiment.neg){
                reviewHolder.tvSentiment.setText("");
            }else if(review.sentiment.pos > review.sentiment.neg && review.sentiment.pos > review.sentiment.neu){
                reviewHolder.tvSentiment.setText("recommended");
            }else if(review.sentiment.neg > review.sentiment.pos && review.sentiment.neg > review.sentiment.neu){
                reviewHolder.tvSentiment.setText("not recommended");
            }else{
                reviewHolder.tvSentiment.setText("");
            }
        }else{
            super.onBindViewHolder(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
