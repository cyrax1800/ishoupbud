package com.project.ishoupbud.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.utils.ValidationUtils;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/12/17.
 */

public class EditProfileActivity extends BaseActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    final CharSequence[] items = {"Take Photo", "Select Photo", "Cancel"};

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_profile_pic) ImageView ivProfile;
    @BindView(R.id.btn_edit_photo) Button btnChangePhoto;
    @BindView(R.id.btn_save) Button btnSave;
    @BindView(R.id.btn_map) Button btnSelectMap;
    @BindView(R.id.et_name) EditText etName;
    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.etl_email) TextInputLayout etlEmail;
    @BindView(R.id.et_phone) EditText etPhoneNo;
    @BindView(R.id.et_address) EditText etAddress;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.etl_password) TextInputLayout etlPassword;

    AlertDialog mediaAlertDialog;

    User user;

    Boolean isChangePicture, isChangeName, isChangeEmail, isChangePhone, isChangeAddress;

    String picBinary, name, email, phone, address, password;
    Double lanagitude, latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnChangePhoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSelectMap.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        mediaAlertDialog = builder.create();

        Log.d(TAG, "onCreate: "+ SharedPref.getValueString(ConstClass.USER));

        user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER), User.class);
        updateView();

    }

    public void updateView(){
        Glide.with(EditProfileActivity.this)
                .load(user.picture_url)
                .placeholder(TextImageCircleHelper.getInstance().getImage(user.name))
                .centerCrop()
                .crossFade()
                .into(ivProfile);

        etEmail.setText(user.email);
        etName.setText(user.name);
        etPhoneNo.setText(user.phone);
        etAddress.setText(user.address);
    }

    public boolean checkEmailValid(){
        if(!ValidationUtils.isEmailValid(email)){
            etlEmail.setError("Email is not valid");
            return false;
        }else{
            etlEmail.setError("");
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
                if(checkEmailValid()){

                }
                break;
            case R.id.btn_edit_photo:
                mediaAlertDialog.show();
                break;
            case R.id.btn_map:
                Intent i = new Intent(this, SelectLocationActivity.class);
                startActivity(i);
                break;
        }
    }
}
