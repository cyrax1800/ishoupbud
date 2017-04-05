package com.project.ishoupbud.view.activities;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.michael.base.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 3/26/17.
 */

public class RegisterActivity extends BaseActivity implements View.OnFocusChangeListener {

    @BindView(R.id.tv_login) TextView tvLogin;
    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.et_name) EditText etName;
    @BindView(R.id.et_phone) EditText etPhoneNumber;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.et_conf_password) EditText etConfPassword;
    @BindView(R.id.btn_register) Button btnRegister;

    //Text Input Layout
    @BindView(R.id.etl_email) TextInputLayout ttlEmail;
    @BindView(R.id.etl_password) TextInputLayout ttlPassword;
    @BindView(R.id.etl_name) TextInputLayout ttlName;
    @BindView(R.id.etl_phone) TextInputLayout ttlPhoneNo;
    @BindView(R.id.etl_conf_password) TextInputLayout ttlConfirmPassword;

    String email, password, confPassword, name, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_register);

        ButterKnife.bind(this);

        // Set blue link underscore
        String htmlString="<u>Login Here</u>";
        tvLogin.setText(Html.fromHtml(htmlString));

        tvLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        etEmail.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);
        etName.setOnFocusChangeListener(this);
        etConfPassword.setOnFocusChangeListener(this);
        etPhoneNumber.setOnFocusChangeListener(this);

    }

    public boolean validation(){
        boolean isValid = true;

        if(!ValidationUtils.isPasswordValid(password,confPassword)){
            isValid =false;
            etConfPassword.requestFocus();
        }

        if(!checkConfPasswordValid()){
            isValid = false;
            etConfPassword.requestFocus();
        }

        if(!checkPasswordValid()){
            isValid = false;
            etPassword.requestFocus();
        }

        if(!checkPhoneNumberValid()){
            isValid = false;
            etPhoneNumber.requestFocus();
        }

        if(!checkEmailValid()){
            isValid = false;
            etEmail.requestFocus();
        }

        return isValid;
    }

    public boolean checkEmailValid(){
        if(!ValidationUtils.isEmailValid(email)){
            ttlEmail.setError("Email is not valid");
            return false;
        }else{
            ttlEmail.setError("");
        }
        return true;
    }

    public boolean checkPasswordValid(){
        if(!ValidationUtils.isPasswordValid(password)){
            ttlPassword.setError("Password must have min length 4");
            return false;
        }else{
            ttlPassword.setError("");
        }
        return true;
    }

    public boolean checkConfPasswordValid(){
        if(!ValidationUtils.isPasswordValid(confPassword)){
            ttlConfirmPassword.setError("Password must have min length 4");
            return false;
        }else{
            ttlConfirmPassword.setError("");
        }
        return true;
    }

    public boolean checkPhoneNumberValid(){
        if(!ValidationUtils.isPhoneNumberValid(phoneNumber)){
            ttlPhoneNo.setError("Phone Number is not valid.");
            return false;
        }else{
            ttlPhoneNo.setError("");
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                confPassword = etConfPassword.getText().toString();
                name = etName.getText().toString();
                phoneNumber = etPhoneNumber.getText().toString();
                if(validation()){

                }else {

                }
                break;
            case R.id.tv_login:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            switch (v.getId()){
                case R.id.et_email:
                    email = etEmail.getText().toString();
                    checkEmailValid();
                    break;
                case R.id.et_name:
                    break;
                case R.id.et_password:
                    password = etPassword.getText().toString();
                    checkPasswordValid();
                    break;
                case R.id.et_conf_password:
                    confPassword = etPassword.getText().toString();
                    checkConfPasswordValid();
                    break;
                case R.id.et_phone:
                    phoneNumber = etPhoneNumber.getText().toString();
                    checkPhoneNumberValid();
                    break;
            }
        }
    }
}
