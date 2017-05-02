package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Token;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.repositories.AuthenticationRepo;
import com.project.ishoupbud.api.repositories.UserRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.ishoupbud.view.dialog.ForgotPasswordDialogFragment;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Settings;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

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

    ForgotPasswordDialogFragment forgotPasswordDialogFragment;

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

        forgotPasswordDialogFragment = new ForgotPasswordDialogFragment();

        initProgressDialog("Loging In...");
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

    public void getOwnData(){
        Call<User> ownData = APIManager.getRepository(UserRepo.class).getOwnData();
        ownData.enqueue(new APICallback<User>() {
            @Override
            public void onSuccess(Call<User> call, Response<User> response) {
                super.onSuccess(call, response);
                progressDialog.dismiss();

                if(getIntent().hasExtra(ConstClass.REGISTER_EXTRA)){
                    Intent i = new Intent();
                    i.putExtra(ConstClass.USER, GsonUtils.getJsonFromObject(response.body(),User.class));
                    setResult(ProfileFragment.RESULT_SUCCESS,i);
                }
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
            }
        });

    }

    public void login(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("grant_type", "password");
        map.put("client_id", Settings.getClientID());
        map.put("client_secret", Settings.getclientSecret());
        map.put("username", email);
        map.put("password", password);
        Call<Token> login = APIManager.getRepository(AuthenticationRepo.class).login(map);
        login.enqueue(new APICallback<Token>() {
            @Override
            public void onSuccess(Call<Token> call, Response<Token> response) {
                super.onSuccess(call, response);
                Log.d(TAG, "onSuccess: " + response.body().access_token);
                SharedPref.save(SharedPref.ACCESS_TOKEN,response.body().access_token);

                getOwnData();
            }

            @Override
            public void onUnauthorized(Call<Token> call, Response<Token> response) {
                super.onUnauthorized(call, response);
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,"Email or Password is wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotFound(Call<Token> call, Response<Token> response) {
                super.onNotFound(call, response);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if(validation()){
                    progressDialog.show();
                    login();
                }
                break;
            case R.id.tv_register:
                setResult(ProfileFragment.RESULT_FOR_REGISTER);
                finish();
                break;
            case R.id.tv_forgot_password:
                forgotPasswordDialogFragment.show(getSupportFragmentManager(),"Forget Password");
                break;
        }
    }
}
