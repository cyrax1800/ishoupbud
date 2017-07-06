package com.project.ishoupbud.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Review;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.view.activities.ProductActivity;
import com.project.ishoupbud.view.fragment.ProductReviewFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/12/17.
 */

public class AddEditReviewDialogFragment extends DialogFragment implements View.OnClickListener{

    RatingBar ratingBar;
    EditText etReview;
    Button btnSubmit;
    Button btnCancel;
    ProgressDialog progressDialog;

    private Review review;
    int productId;
    int vendorId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_add_edit_review,container,false);

        ratingBar = (RatingBar) rootView.findViewById(R.id.df_review_rating_star);
        etReview = (EditText) rootView.findViewById(R.id.df_review_desc);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);

        if(review != null){
            getDialog().setTitle("Edit Review");
            ratingBar.setRating((float)review.rating);
            etReview.setText(review.description);
        }else{
            getDialog().setTitle("Add Review");
        }

        progressDialog = new ProgressDialog(getContext());

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return rootView;
    }

    public void setReview(Review review){
        this.review = review;
    }

    public void setProductAndVendorId(int productId, int vendorId){
        this.productId = productId;
        this.vendorId = vendorId;
    }

    public boolean validate(){
        if(ratingBar.getRating() == 0){
            Toast.makeText(getContext(), "Rating cannot be 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etReview.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Review cannot be black", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.review = null;
        ratingBar.setRating(0);
        etReview.setText("");
        super.onDismiss(dialog);
    }

    public void addOrEditReview(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_id", productId);
        map.put("vendor_id", vendorId);
        map.put("rating", ratingBar.getRating());
        map.put("body", etReview.getText().toString());
        if(review != null){
            progressDialog.setMessage("Updating review");
            Call<GenericResponse<Review>> updateReview = APIManager.getRepository(ReviewRepo.class).updateReview(map, review.id);
            updateReview.enqueue(new APICallback<GenericResponse<Review>>() {
                @Override
                public void onSuccess(Call<GenericResponse<Review>> call, Response<GenericResponse<Review>> response) {
                    super.onSuccess(call, response);
                    progressDialog.dismiss();
                    dismiss();
                }

                @Override
                public void onError(Call<GenericResponse<Review>> call, Response<GenericResponse<Review>> response) {
                    super.onError(call, response);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<GenericResponse<Review>> call, Throwable t) {
                    super.onFailure(call, t);
                    progressDialog.dismiss();
                }
            });
        }else{
            progressDialog.setMessage("Adding review");
            Call<GenericResponse<Review>> addReivew = APIManager.getRepository(ReviewRepo.class).submitReview(map);
            addReivew.enqueue(new APICallback<GenericResponse<Review>>() {
                @Override
                public void onCreated(Call<GenericResponse<Review>> call, Response<GenericResponse<Review>> response) {
                    super.onCreated(call, response);
                    progressDialog.dismiss();
                    dismiss();
                }

                @Override
                public void onError(Call<GenericResponse<Review>> call, Response<GenericResponse<Review>> response) {
                    super.onError(call, response);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<GenericResponse<Review>> call, Throwable t) {
                    super.onFailure(call, t);
                    progressDialog.dismiss();
                }
            });
        }
        progressDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if(validate()){
                    addOrEditReview();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
