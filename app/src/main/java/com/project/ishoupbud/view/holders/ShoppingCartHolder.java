package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.ishoupbud.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/13/17.
 */

public class ShoppingCartHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_product) public ImageView ivProduct;
    @BindView(R.id.tv_product_name) public TextView tvProductName;
    @BindView(R.id.tv_product_price) public TextView tvPrice;
    @BindView(R.id.tv_vendor) public TextView tvVendor;
    @BindView(R.id.ib_delete) public ImageButton iBtnDelete;

    @BindView(R.id.btn_plus_stepper) public ImageButton ibtnPlusStepper;
    @BindView(R.id.btn_minus_stepper) public ImageButton ibtnMinusStepper;
    @BindView(R.id.et_stepper_count) public EditText etStepperCount;

    public ShoppingCartHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
