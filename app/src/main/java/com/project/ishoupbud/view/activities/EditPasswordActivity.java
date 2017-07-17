package com.project.ishoupbud.view.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.repositories.UserRepo;
import com.project.ishoupbud.helper.DialogMessageHelper;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.Response;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

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
//    @BindView(R.id.btn_save) Button btnSave;

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

//        btnSave.setOnClickListener(this);

        initProgressDialog("Changing Password...");
    }

    public boolean validation(){
        boolean isValid = true;

        currentPassword = etCurrentPassword.getText().toString();
        password = etNewPassword.getText().toString();
        rePassword = etRePassword.getText().toString();

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

        if(!ValidationUtils.isPasswordValid(password,rePassword)){
            isValid =false;
            etRePassword.requestFocus();
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

    public void changePassword(){
        progressDialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("current_password", currentPassword);
        map.put("password", password);
        map.put("password_confirmation", rePassword);
        Call<Response> changePasswordCall = APIManager.getRepository(UserRepo.class).changePassword(map);
        changePasswordCall.enqueue(new APICallback<Response>() {
            @Override
            public void onSuccess(Call<Response> call, retrofit2.Response<Response> response) {
                super.onSuccess(call, response);
                progressDialog.dismiss();
                DialogMessageHelper.getInstance().show(EditPasswordActivity.this, "Password Successfully changed", "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogMessageHelper.getInstance().dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onError(Call<Response> call, retrofit2.Response<Response> response) {
                super.onError(call, response);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Fail to change password, please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Someting wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }else if(item.getItemId() == R.id.action_save){
            if(validation()){
                changePassword();
            }else{
                Toast.makeText(getApplicationContext(), "Confirmation Password is wrong", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(ProfileFragment.RESULT_NO_CHANGES);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_save:
//                if(validation()){
//                    changePassword();
//                }else{
//                    Toast.makeText(getApplicationContext(), "Confirmation Password is wrong", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
}
