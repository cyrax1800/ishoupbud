package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.view.adapters.ShoppingCartContainerAdapter;
import com.project.ishoupbud.view.adapters.ShoppingCartAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 6/27/17.
 */

public class ShoppingCartContainerHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ll_chk_vendor) public LinearLayout llChkVendor;
    @BindView(R.id.chk_vendor) public CheckBox chkVendor;
    @BindView(R.id.rv_cart) public RecyclerView rvCart;
    @BindView(R.id.bottom_container) public LinearLayout btmContainer;
    @BindView(R.id.tv_sub_total) public TextView tvSubTotal;
    @BindView(R.id.btn_bayar) public Button btnBayar;

    public ShoppingCartAdapter<ShoppingCart> shoppingCartAdapter;

    public ShoppingCartContainerHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        shoppingCartAdapter = new ShoppingCartAdapter<>();
//        shoppingCartAdapter.BindTo(this);
    }

    public ShoppingCartContainerHolder(View itemView, ShoppingCartContainerAdapter adapter,
                                       int position) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        shoppingCartAdapter = new ShoppingCartAdapter<>(this, adapter, position);
    }

    public void setBind(ShoppingCartContainerAdapter adapter, int position) {
        shoppingCartAdapter.bindTo(this, adapter, position);
    }
}
