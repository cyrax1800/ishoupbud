package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.project.ishoupbud.R;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/12/17.
 */

public class EditPasswordActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.et_current_password) EditText etCurrentPassword;
    @BindView(R.id.etl_current_password) TextInputLayout etlCurrentPassword;
    @BindView(R.id.et_password) EditText etNewPassword;
    @BindView(R.id.etl_password) TextInputLayout etlNewPassword;
    @BindView(R.id.et_conf_password) EditText etRePassword;
    @BindView(R.id.etl_conf_password) TextInputLayout etlRePassword;
    @BindView(R.id.btn_save) Button btnSave;

    String currentPassword, password, rePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        ButterKnife.bind(this);

        toolbar.setTitle("Edit Password");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSave.setOnClickListener(this);
    }

    public boolean validation(){
        boolean isValid = true;

        if(!ValidationUtils.isPasswordValid(password,rePassword)){
            isValid =false;
            etRePassword.requestFocus();
        }

        if(!checkConfPasswordValid()){
            isValid = false;
            etRePassword.requestFocus();
        }

        if(!checkPasswordValid()){
            isValid = false;
            etNewPassword.requestFocus();
        }

        if(!checkCurrentPasswordValid()){
            isValid = false;
            etCurrentPassword.requestFocus();
        }

        return isValid;
    }

    public boolean checkCurrentPasswordValid(){
        if(!ValidationUtils.isPasswordValid(currentPassword)){
            etlCurrentPassword.setError("Password must have min length 4");
            return false;
        }else{
            etlCurrentPassword.setError("");
        }
        return true;
    }

    public boolean checkPasswordValid(){
        if(!ValidationUtils.isPasswordValid(password)){
            etlNewPassword.setError("Password must have min length 4");
            return false;
        }else{
            etlNewPassword.setError("");
        }
        return true;
    }

    public boolean checkConfPasswordValid(){
        if(!ValidationUtils.isPasswordValid(rePassword)){
            etlRePassword.setError("Password must have min length 4");
            return false;
        }else{
            etlRePassword.setError("");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(ProfileFragment.RESULT_NO_CHANGES);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                if(validation()){

                }
                break;
        }
    }
}
