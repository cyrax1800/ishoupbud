package com.project.ishoupbud.view.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.response.ProductAllReviewResponse;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.view.adapters.ReviewAdapter;
import com.project.ishoupbud.view.dialog.AddEditReviewDialogFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.utils.DateUtils;
import com.project.michael.base.views.BaseFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/10/17.
 */

public class ProductReviewFragment extends BaseFragment {

//    @BindView(R.id.ll_own_review)
//    LinearLayout llOwnReview;
//    @BindView(R.id.iv_review)
//    ImageView ivOwnProfilePic;
//    @BindView(R.id.tv_review_name)
//    TextView tvUserName;
//    @BindView(R.id.tv_review_date)
//    TextView tvReviewDate;
//    @BindView(R.id.tv_review_description)
//    TextView tvReviewDesc;
//    @BindView(R.id.rating_bar_review)
//    RatingBar ratingBar;
//    @BindView(R.id.btn_delete)
//    ImageButton iBtnDelete;
//    @BindView(R.id.btn_edit)
//    ImageButton iBtnEdit;
//
//    @BindView(R.id.btn_write_review)
//    Button btnWriteReview;
    @BindView(R.id.spinner_time)
    Spinner spinnerTimeFilter;
    @BindView(R.id.spinner_vendor)
    Spinner spinnerVendorFilter;
    @BindView(R.id.rv_review)
    public RecyclerView rvReview;

    ReviewAdapter<Review> reviewAdapter;
//    AddEditReviewDialogFragment addEditReviewDialogFragment;
    AlertDialog deleteReviewDialog;
    ProgressDialog progressDialog;

    public boolean hasOwnReview;
    public Review ownReivew;

    public String order;
    public int productId;
    public int selectedVendorId;
    public List<Vendor> vendorList;
    private ArrayAdapter<String> vendorNameList;

    Call<ProductAllReviewResponse> getReview;
    public boolean isCanEnableFromParent;
    public boolean isRecyclerviewInTop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_review_product, container, false);

            ButterKnife.bind(this, _rootView);

//            updateOwnReviewView();

//            btnWriteReview.setOnClickListener(this);

            selectedVendorId = -1;
            isCanEnableFromParent = false;
            isRecyclerviewInTop = true;

            vendorNameList = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            spinnerVendorFilter.setAdapter(vendorNameList);
            spinnerVendorFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVendorId = position - 1;
                    requestReview(1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerTimeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0){
                        order = "Newest";
                    }else if(position == 1){
                        order = "rate_high";
                    }else if(position == 2){
                        order = "rate_low";
                    }
                    requestReview(1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            reviewAdapter = new ReviewAdapter<>(this);

            final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false);

            rvReview.addItemDecoration(new InsetDividerItemDecoration(getContext()));
            rvReview.setLayoutManager(layoutManager);
            rvReview.setAdapter(reviewAdapter);
            rvReview.setNestedScrollingEnabled(false);
            rvReview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                    if(firstVisibleItem == 0){
                        Log.d(TAG, "onScrolled: Review ");
                        if(isCanEnableFromParent){
                            recyclerView.setNestedScrollingEnabled(true);
                            isCanEnableFromParent = false;
                        }else{
                            recyclerView.setNestedScrollingEnabled(false);
                        }
//                        if(!recyclerView.isNestedScrollingEnabled()){
//                            recyclerView.setNestedScrollingEnabled(true);
//                        }else{
//                            recyclerView.setNestedScrollingEnabled(false);
//                        }
                        // your code
                    }else{
                        recyclerView.setNestedScrollingEnabled(false);
                    }
                }
            });
            rvReview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int firstVisibleItem = ((LinearLayoutManager)rvReview.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
                    int lastVisibleItem = ((LinearLayoutManager)rvReview.getLayoutManager())
                            .findLastCompletelyVisibleItemPosition();
                    if(isCanEnableFromParent && !((firstVisibleItem == 0) && lastVisibleItem ==
                            reviewAdapter.getItemCount() - 1)){
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
                }
            });

//            addEditReviewDialogFragment = new AddEditReviewDialogFragment();
//            addEditReviewDialogFragment.setProductReviewFragment(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Detele Review")
                    .setMessage("Are you sure want to delete your review of this product?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteOwnReview();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteReviewDialog.dismiss();
                        }
                    });

            deleteReviewDialog = builder.create();

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("deleting Review");
        }

        return _rootView;
    }

//    public void updateOwnReviewView(){
//        if (hasOwnReview) {
//            llOwnReview.setVisibility(View.VISIBLE);
//            btnWriteReview.setVisibility(View.GONE);
//
//            Glide
//                    .with(this)
//                    .load(ownReivew.user.getSmallImage())
//                    .placeholder(TextImageCircleHelper.getInstance().getImage(ownReivew.user.name))
//                    .centerCrop()
//                    .crossFade()
//                    .into(ivOwnProfilePic);
//
//            tvUserName.setText(ownReivew.user.name);
//            tvReviewDate.setText(DateUtils.getDate(ownReivew.date.getTime()));
//            tvReviewDesc.setText(ownReivew.description);
//            ratingBar.setRating((float) ownReivew.rating);
//            iBtnDelete.setOnClickListener(this);
//            iBtnEdit.setOnClickListener(this);
//        } else {
//            llOwnReview.setVisibility(View.GONE);
//            btnWriteReview.setVisibility(View.VISIBLE);
//        }
//    }

    public void setProductId(int id) {
        this.productId = id;
        requestReview(1);
    }

    public void setVendor(List<Vendor> vendors) {
        this.vendorList = vendors;
        vendorNameList.clear();
        vendorNameList.add("All");
        for (int i = 0; i < vendors.size(); i++) {
            vendorNameList.add(vendors.get(i).name);
        }
        vendorNameList.notifyDataSetChanged();
        if(selectedVendorId >= 0){
            for(int i = 0; i< vendors.size(); i++){
                if(vendors.get(i).id == selectedVendorId){
                    spinnerVendorFilter.setSelection(i + 1);
                }
            }
        }
//        addEditReviewDialogFragment.setVendors(vendorNameList);
    }

    public void requestReview(int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", productId);
        map.put("order", order);
        if(selectedVendorId > -1){
            map.put("vendor_id", vendorList.get(selectedVendorId).id);
        }
        map.put("page", page);
        map.put("perpage", 10);
        if((getReview != null) && getReview.isExecuted()){
            getReview.cancel();
        }
        getReview = APIManager.getRepository(ReviewRepo.class).getReview(map);
        getReview.enqueue(new APICallback<ProductAllReviewResponse>() {
            @Override
            public void onSuccess(Call<ProductAllReviewResponse> call, Response<ProductAllReviewResponse> response) {
                super.onSuccess(call, response);
                reviewAdapter.setNew(response.body().reviews);
            }

            @Override
            public void onFailure(Call<ProductAllReviewResponse> call, Throwable t) {
                super.onFailure(call, t);
            }
        });

    }

    public void deleteOwnReview(){
        progressDialog.show();
        Call<com.project.michael.base.models.Response> deleteReview = APIManager.getRepository(ReviewRepo.class).deleteReview(ownReivew.id);
        deleteReview.enqueue(new APICallback<com.project.michael.base.models.Response>() {
            @Override
            public void onNoContent(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                super.onNoContent(call, response);
                hasOwnReview = false;
                ownReivew = null;
//                updateOwnReviewView();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<com.project.michael.base.models.Response> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_write_review:
//                addEditReviewDialogFragment.show(getFragmentManager(), "addEditReviewDialog");
//                break;
            case R.id.btn_delete:
                deleteReviewDialog.show();
                break;
            case R.id.btn_edit:
//                addEditReviewDialogFragment.setReview(ownReivew);
//                addEditReviewDialogFragment.show(getFragmentManager(), "addEditReviewDialog");
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
