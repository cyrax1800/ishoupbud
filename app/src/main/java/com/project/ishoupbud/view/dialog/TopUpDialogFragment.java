package com.project.ishoupbud.view.dialog;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Saldo;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.repositories.UserRepo;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.utils.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/12/17.
 */

public class TopUpDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "tmp-topup";

    EditText etNominal;
    Button btnSubmit;
    Button btnCancel;

    int nominal;

    ProgressDialog progressDialog;

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

        etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(StringUtils.isNullOrEmpty(s.toString())) return;
                String cleanString = s.toString().replaceAll("(^Rp. )|\\.", "");
                if(StringUtils.isNullOrEmpty(cleanString)){
                    etNominal.setText("");
                    return;
                }
                etNominal.removeTextChangedListener(this);
                String formatted = Utils.indonesiaFormat(Double.parseDouble(cleanString), false);
                etNominal.setText(formatted);
                etNominal.setSelection(formatted.length());
                etNominal.addTextChangedListener(this);
            }
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Requesting top-up");

        return rootView;
    }

    public boolean validate(){
        if(etNominal.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"Nominal cant be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        String cleanString = etNominal.getText().toString().replaceAll("(^Rp. )|\\.", "");
        nominal = Integer.parseInt(cleanString);
        if(nominal == 0){
            Toast.makeText(getContext(),"Nominal cant be 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void addSaldo(){
        progressDialog.show();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("nominal",nominal);
        APIManager.getRepository(UserRepo.class)
            .addSaldo(map)
            .enqueue(new APICallback<GenericResponse<Saldo>>() {
                @Override
                public void onSuccess(Call<GenericResponse<Saldo>> call, Response<GenericResponse<Saldo>> response) {
                    super.onSuccess(call, response);
                    progressDialog.dismiss();
                    dismiss();
                }

                @Override
                public void onUnprocessableEntity(Call<GenericResponse<Saldo>> call, Response<GenericResponse<Saldo>> response) {
                    super.onUnprocessableEntity(call, response);
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Harus diatas 10000", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<GenericResponse<Saldo>> call, Throwable t) {
                    super.onFailure(call, t);
                    progressDialog.dismiss();
                }

                @Override
                public void onError(Call<GenericResponse<Saldo>> call, Response<GenericResponse<Saldo>> response) {
                    super.onError(call, response);
                    progressDialog.dismiss();
                }
            });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if(validate()){
                    addSaldo();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
