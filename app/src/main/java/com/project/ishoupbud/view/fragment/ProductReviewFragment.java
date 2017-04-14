package com.project.ishoupbud.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.view.adapters.ReviewAdapter;
import com.project.ishoupbud.view.dialog.AddEditReviewDialogFragment;
import com.project.michael.base.utils.DateUtils;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/10/17.
 */

public class ProductReviewFragment extends BaseFragment {

    @BindView(R.id.ll_own_review) LinearLayout llOwnReview;
    @BindView(R.id.iv_review) ImageView ivOwnProfilePic;
    @BindView(R.id.tv_review_name) TextView tvUserName;
    @BindView(R.id.tv_review_date) TextView tvReviewDate;
    @BindView(R.id.tv_review_description) TextView tvReviewDesc;
    @BindView(R.id.rating_bar_review) RatingBar ratingBar;
    @BindView(R.id.btn_delete) ImageButton iBtnDelete;
    @BindView(R.id.btn_edit) ImageButton iBtnEdit;

    @BindView(R.id.btn_write_review) Button btnWriteReview;
    @BindView(R.id.spinner_time) Spinner spinnerTimeFilter;
    @BindView(R.id.spinner_vendor) Spinner spinnerVendorFilter;
    @BindView(R.id.rv_review) RecyclerView rvReview;

    ReviewAdapter<Review> reviewAdapter;
    AddEditReviewDialogFragment addEditReviewDialogFragment;
    AlertDialog deleteReviewDialog;

    boolean hasOwnReview;
    Review ownReivew;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_review_product, container, false);

            ButterKnife.bind(this, _rootView);

            if(hasOwnReview){
                llOwnReview.setVisibility(View.VISIBLE);
                btnWriteReview.setVisibility(View.GONE);

                Glide
                        .with(this)
//                .load(product.picUrl)
                        .load("http://kingofwallpapers.com/aqua/aqua-001.jpg")
                        .centerCrop()
                        .crossFade()
                        .into(ivOwnProfilePic);

                tvUserName.setText(ownReivew.user.name);
                tvReviewDate.setText(DateUtils.getDate(ownReivew.date.getTime()));
                tvReviewDesc.setText(ownReivew.description);
                ratingBar.setRating((float)ownReivew.rating);
                iBtnDelete.setOnClickListener(this);
                iBtnEdit.setOnClickListener(this);
            }else{
                llOwnReview.setVisibility(View.GONE);
                btnWriteReview.setVisibility(View.VISIBLE);
            }

            btnWriteReview.setOnClickListener(this);

            reviewAdapter = new ReviewAdapter<>();

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);

            rvReview.addItemDecoration(new InsetDividerItemDecoration(getContext()));
            rvReview.setLayoutManager(layoutManager);
            rvReview.setAdapter(reviewAdapter);

            addEditReviewDialogFragment = new AddEditReviewDialogFragment();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Detele Review")
                    .setMessage("Are you sure want to delete your review of this product?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteReviewDialog.dismiss();
                        }
                    });

            deleteReviewDialog = builder.create();

        }

        return _rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_write_review:
                addEditReviewDialogFragment.show(getFragmentManager(),"addEditReviewDialog");
                break;
            case R.id.btn_delete:
                deleteReviewDialog.show();
                break;
            case R.id.btn_edit:
                addEditReviewDialogFragment.show(getFragmentManager(),"addEditReviewDialog");
                break;
        }
    }

    public static ProductReviewFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProductReviewFragment fragment = new ProductReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
