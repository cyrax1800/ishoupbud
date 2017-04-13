package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.view.adapters.ShoppingCartAdapter;
import com.project.michael.base.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/12/17.
 */

public class ShoppingCartActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_wishlist) RecyclerView rvShoppingCart;
    @BindView(R.id.tv_total_price) TextView tvTotalPrice;
    @BindView(R.id.btn_continue) Button btnContinue;

    ShoppingCartAdapter<ShoppingCart> shoppingCartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        ButterKnife.bind(this);

        toolbar.setTitle("Shopping Cart");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnContinue.setOnClickListener(this);
        //TODO make adapter

        shoppingCartAdapter = new ShoppingCartAdapter<>();
        shoppingCartAdapter.setNew(ShoppingCart.getDummy(10));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        rvShoppingCart.addItemDecoration(new InsetDividerItemDecoration(this));
        rvShoppingCart.setLayoutManager(layoutManager);
        rvShoppingCart.setAdapter(shoppingCartAdapter);
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
            case R.id.btn_continue:
                break;
        }
    }
}
