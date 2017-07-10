package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ShoppingCartContainerAdapter;
import com.project.ishoupbud.view.holders.ShoppingCartContainerHolder;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.listeners.ClickEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/12/17.
 */

public class ShoppingCartActivity extends BaseActivity {

    public static final int REQUEST_MAP = 0;
    @BindView(R.id.bottom_container) public LinearLayout llBtnContainer;
    @BindView(R.id.chk_select_all_transaction) public CheckBox chkAllTransaction;
    public float totalPrice = 0;
    public boolean isAllNotChecked = true;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nestedScroll) NestedScrollView nestedScroll;
    @BindView(R.id.ll_chk_all_transaction) LinearLayout llChkAllTransaction;
    @BindView(R.id.rv_cart_container) RecyclerView rvShoppingCart;
    @BindView(R.id.tv_total_price) TextView tvTotalPrice;
    @BindView(R.id.btn_continue) Button btnContinue;
    ShoppingCartContainerAdapter<ShoppingCartContainer> shoppingCartContainerAdapter;

    int selectedIdx;
    Vendor vendor;
    int bottomContainerHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        ButterKnife.bind(this);

        toolbar.setTitle("Shopping Cart");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnContinue.setOnClickListener(this);
        llChkAllTransaction.setOnClickListener(this);

        llBtnContainer.post(new Runnable() {
            @Override
            public void run() {
                bottomContainerHeight = llBtnContainer.getHeight();
                llBtnContainer.setVisibility(View.GONE);
            }
        });

        chkAllTransaction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkAllTransaction.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    chkAllTransaction.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        //TODO make adapter

        shoppingCartContainerAdapter = new ShoppingCartContainerAdapter<>(this);
        shoppingCartContainerAdapter.setItemListener(R.id.ll_chk_vendor, new
                ClickEventListener<ShoppingCartContainer>() {
                    @Override
                    public void onClick(View v, ShoppingCartContainer shoppingCartContainer, int
                            position) {
                        Log.d(TAG, "onClick: asdasdasd");
                        shoppingCartContainerAdapter.toggleChecked((ShoppingCartContainerHolder)
                                rvShoppingCart.findViewHolderForAdapterPosition(position),
                                position);
                    }
                }).setItemListener(R.id.btn_bayar, new ClickEventListener<ShoppingCartContainer>() {
            @Override
            public void onClick(View v, final ShoppingCartContainer shoppingCartContainer, int
                    position) {
                konfirmasiPembayaran(Collections.singletonList(shoppingCartContainer));
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        rvShoppingCart.setNestedScrollingEnabled(false);
        rvShoppingCart.setLayoutManager(layoutManager);
        rvShoppingCart.setAdapter(shoppingCartContainerAdapter);

        tvTotalPrice.setText("Total: Rp. 0");

        initProgressDialog("Deleting Items...");

        getCart();
    }

    public void getCart() {
        Call<GenericResponse<List<ShoppingCartContainer>>> getCartRequest = APIManager
                .getRepository(ShoppingCartRepo.class).getCart();
        getCartRequest.enqueue(new APICallback<GenericResponse<List<ShoppingCartContainer>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<ShoppingCartContainer>>> call,
                                  Response<GenericResponse<List<ShoppingCartContainer>>> response) {
                super.onSuccess(call, response);
                List<ShoppingCartContainer> shoppingCartContainers = response.body().data;
                shoppingCartContainerAdapter.setNew(shoppingCartContainers);
                shoppingCartContainerAdapter.checkedIdx.clear();
                for (int i = 0; i < shoppingCartContainerAdapter.getItemCount(); i++) {
                    shoppingCartContainerAdapter.checkedIdx.add(false);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<ShoppingCartContainer>>> call,
                                  Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void toggleCheckFromClick() {
        boolean isChecked = chkAllTransaction.isChecked();
        isChecked = !isChecked;
        chkAllTransaction.setChecked(isChecked);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                nestedScroll.getLayoutParams();
        marginLayoutParams.setMargins(0, 0, 0, 0);
        llBtnContainer.setVisibility(View.GONE);
        if (isChecked) {
            llBtnContainer.setVisibility(View.VISIBLE);
            marginLayoutParams.setMargins(0, 0, 0, bottomContainerHeight);
        }
        nestedScroll.setLayoutParams(marginLayoutParams);
        for (int i = 0; i < shoppingCartContainerAdapter.getItemCount(); i++) {
            shoppingCartContainerAdapter.setCheck((ShoppingCartContainerHolder) rvShoppingCart
                    .findViewHolderForAdapterPosition(i), i, isChecked);
        }
        calculateTotal();
    }

    public void validateChecklist() {
        boolean isAllChecked = true;
        boolean isAllNotChecked = true;
        boolean hasItemIsChecked = false;
        totalPrice = 0;
        for (int i = 0; i < shoppingCartContainerAdapter.getItemCount(); i++) {
            if (shoppingCartContainerAdapter.checkedIdx.get(i)) {
                hasItemIsChecked = true;
                isAllNotChecked = false;
                totalPrice += shoppingCartContainerAdapter.getItemAt(i).subTotal;
            } else {
                isAllChecked = false;
            }
        }

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                nestedScroll.getLayoutParams();
        marginLayoutParams.setMargins(0, 0, 0, 0);
        llBtnContainer.setVisibility(View.GONE);
        if (isAllChecked) {
            chkAllTransaction.setChecked(true);
        } else {
            if (hasItemIsChecked) {
                chkAllTransaction.setChecked(false);
            }
        }
        if (isAllNotChecked) {
            chkAllTransaction.setChecked(false);
        }
        if (hasItemIsChecked) {
            llBtnContainer.setVisibility(View.VISIBLE);
            marginLayoutParams.setMargins(0, 0, 0, bottomContainerHeight);
            for (int i = 0; i < shoppingCartContainerAdapter.getItemCount(); i++) {
                ShoppingCartContainerHolder shoppingCartContainerHolder =
                        (ShoppingCartContainerHolder) rvShoppingCart
                                .findViewHolderForAdapterPosition(i);
                shoppingCartContainerHolder.btmContainer.setVisibility(View.GONE);
            }
        } else {
            for (int i = 0; i < shoppingCartContainerAdapter.getItemCount(); i++) {
                ShoppingCartContainerHolder shoppingCartContainerHolder =
                        (ShoppingCartContainerHolder) rvShoppingCart
                                .findViewHolderForAdapterPosition(i);
                shoppingCartContainerHolder.btmContainer.setVisibility(View.VISIBLE);
            }
        }
        nestedScroll.setLayoutParams(marginLayoutParams);
        calculateTotal();
    }

    public void calculateTotal() {
        totalPrice = 0;
        for (int i = 0; i < shoppingCartContainerAdapter.getItemCount(); i++) {
            if (shoppingCartContainerAdapter.checkedIdx.get(i)) {
                totalPrice += shoppingCartContainerAdapter.getItemAt(i).subTotal;
            }
        }
        tvTotalPrice.setText(Utils.indonesiaFormat(totalPrice));
    }

    public void konfirmasiPembayaran(List<ShoppingCartContainer> list) {
        Intent intent = new Intent(this, BayarActivity.class);
        intent.putExtra(ConstClass.CART_EXTRA, GsonUtils.getJsonFromObject(list));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                List<ShoppingCartContainer> tmpList = new ArrayList<>();
                for (int i = 0; i < shoppingCartContainerAdapter.checkedIdx.size(); i++) {
                    if (shoppingCartContainerAdapter.checkedIdx.get(i))
                        tmpList.add(shoppingCartContainerAdapter.getItemAt(i));
                }
                konfirmasiPembayaran(tmpList);
                break;
            case R.id.ll_chk_all_transaction:
                if (shoppingCartContainerAdapter.getItemCount() == 0) return;
                toggleCheckFromClick();
                break;
        }
    }
}
