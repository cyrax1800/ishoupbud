package com.project.ishoupbud.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/12/17.
 */

public class AddEditReviewDialogFragment extends DialogFragment implements View.OnClickListener{

    Spinner spinnerVendor;
    RatingBar ratingBar;
    EditText etReview;
    Button btnSubmit;
    Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_add_edit_review,container,false);
        getDialog().setTitle("Add Review");

        spinnerVendor = (Spinner) rootView.findViewById(R.id.spinner_vendor);
        ratingBar = (RatingBar) rootView.findViewById(R.id.df_review_rating_star);
        etReview = (EditText) rootView.findViewById(R.id.df_review_desc);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                break;
            case R.id.btn_cancel:
                break;
        }
        dismiss();
    }
}
