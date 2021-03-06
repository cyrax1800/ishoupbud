package com.project.ishoupbud.view.adapters;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.helper.DialogMessageHelper;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.view.fragment.ProductReviewFragment;
import com.project.ishoupbud.view.holders.ReviewHolder;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.Response;
import com.project.michael.base.utils.DateUtils;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.views.adapters.EndlessScrollAdapter;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by michael on 4/11/17.
 */

public class ReviewAdapter<Model> extends EndlessScrollAdapter<Model> {

    private ProductReviewFragment reviewFragment;
    private ProgressDialog progressDialog;

    public ReviewAdapter(ProductReviewFragment reviewFragment){
        this.reviewFragment = reviewFragment;
        progressDialog = new ProgressDialog(reviewFragment.getContext());
        progressDialog.setMessage("Sedang melaporkan ulasan");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,parent,false);
            return super.onPostCreateViewHolder(new ReviewHolder(itemView), viewType);
        }
        return super.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ReviewHolder){
            final ReviewHolder reviewHolder = (ReviewHolder) holder;
            final Review review = (Review) mModelList.get(position);

            Glide
                    .with(reviewHolder.ivUserProfilePic.getContext())
                    .load(review.user.getSmallImage())
                    .placeholder(TextImageCircleHelper.getInstance().getImage(review.user.name))
                    .centerCrop()
                    .crossFade()
                    .into(reviewHolder.ivUserProfilePic);

            reviewHolder.tvUserName.setText(review.user.name);
            reviewHolder.tvReviewDate.setText(DateUtils.getDate(review.date.getTime()));
            reviewHolder.tvReviewDesc.setText(review.description);
            reviewHolder.ratingBar.setRating((float)review.rating);
            if(review.sentiment.neu > review.sentiment.pos && review.sentiment.neu > review.sentiment.neg){
                reviewHolder.tvSentiment.setText("");
            }else if(review.sentiment.pos > review.sentiment.neg && review.sentiment.pos >= review.sentiment.neu){
                reviewHolder.tvSentiment.setText("Positif");
            }else if(review.sentiment.neg > review.sentiment.pos && review.sentiment.neg >= review.sentiment.neu){
                reviewHolder.tvSentiment.setText("Negatif");
            }else{
                reviewHolder.tvSentiment.setText("Netral");
            }
            reviewHolder.iBtnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    report(position, review);
                }
            });
        }else{
            super.onBindViewHolder(holder,position);
        }
    }

    public void report(final int pos, final Review review){
        DialogMessageHelper.getInstance().show(reviewFragment.getContext(),
                "Melaporkan Ulasan", "Apakah ingin melaporkan ulasan ini?", "Laporkan", new
                        DialogInterface.OnClickListener
                                () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doReport(pos, review);
                            }
                        }, "Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogMessageHelper.getInstance().dismiss();
                    }
                });
    }

    public void doReport(final int pos, final Review review){
        progressDialog.show();
        //Sini untuk melaporkan ulasan
        HashMap<String, Object> map = new HashMap();
        map.put("review_id", review.id);
        APIManager.getRepository(ReviewRepo.class).reportReview(map)
                .enqueue(new APICallback<Response>() {
                    @Override
                    public void onCreated(Call<Response> call, retrofit2.Response<Response>
                            response) {
                        super.onCreated(call, response);
                        progressDialog.dismiss();
                        DialogMessageHelper.getInstance().dismiss();
                        Toast.makeText(reviewFragment.getContext(), "Ulasan berhasil dilaporkan",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call<Response> call, retrofit2.Response<Response>
                            response) {
                        super.onError(call, response);
                        progressDialog.dismiss();
                        Toast.makeText(reviewFragment.getContext(), "Ada kesalahan, mohon diulangi",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        super.onFailure(call, t);
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
