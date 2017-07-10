package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/10/17.
 */

public class VendorHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ll_product_vendor) public LinearLayout llProductVendor;
    @BindView(R.id.radio) public RadioButton radioButton;
    @BindView(R.id.tv_vendor) public TextView tvName;
    @BindView(R.id.tv_alamat) public TextView tvAddress;
    @BindView(R.id.tv_price) public TextView tvPrice;

    public VendorHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}