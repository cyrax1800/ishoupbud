package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.project.ishoupbud.R;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/12/17.
 */

public class EditProfileActivity extends BaseActivity {

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                break;
        }
    }
}
