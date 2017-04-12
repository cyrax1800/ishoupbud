package com.project.ishoupbud.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.project.ishoupbud.R;

/**
 * Created by michael on 4/12/17.
 */

public class TopUpDialogFragment extends DialogFragment implements View.OnClickListener {

    EditText etNominal;
    Button btnSubmit;
    Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_top_up,container,false);
        getDialog().setTitle("Top up");

        etNominal = (EditText) rootView.findViewById(R.id.et_nominal_topup);
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
