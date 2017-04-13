package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/13/17.
 */

public class BuktiTransferActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_total_top_up) TextView tvTotalTopUp;
    @BindView(R.id.tv_current_saldo) TextView tvCurrentSaldo;
    @BindView(R.id.tvPathImage) TextView tvPathImage;
    @BindView(R.id.btn_upload) Button btnUploadPhoto;
    @BindView(R.id.btn_cancel) Button btnCancel;
    @BindView(R.id.btn_confirmation) Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti_transfer);

        ButterKnife.bind(this);

        toolbar.setTitle("Upload Bukti Transfer");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnUploadPhoto.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
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
            case R.id.btn_upload:
                break;
            case R.id.btn_cancel:
                break;
            case R.id.btn_confirmation:
                break;
        }
    }
}
