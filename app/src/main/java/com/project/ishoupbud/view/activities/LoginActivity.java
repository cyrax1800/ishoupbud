package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 3/25/17.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_register) TextView tvRegister;
    @BindView(R.id.tv_forgot_password) TextView tvForgotPassword;
    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.btn_login) Button btnLogin;

    //Text Input Layout
    @BindView(R.id.etl_email) TextInputLayout ttlEmail;
    @BindView(R.id.etl_password) TextInputLayout ttlPassword;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // Set blue link underscore
        String htmlString="<u>Register Here</u>";
        tvRegister.setText(Html.fromHtml(htmlString));

        htmlString ="<u>Forgot Password?</u>";
        tvForgotPassword.setText(Html.fromHtml(htmlString));

        tvForgotPassword.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    email = etEmail.getText().toString();
                    checkEmailValid();
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    password = etPassword.getText().toString();
                    checkPasswordValid();
                }
            }
        });

        if(getIntent().hasExtra(ConstClass.REGISTER_EXTRA)){
            Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(i);
        }
    }

    public boolean validation(){
        boolean isValid = true;

        if(!checkPasswordValid()){
            isValid = false;
            etPassword.requestFocus();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if(validation()){

                }else {

                }
                break;
            case R.id.tv_register:
                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.tv_forgot_password:
                break;
        }
    }
}
