package com.project.ishoupbud.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.api.response.ShoppingCartResponse;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.StepperView;
import com.project.ishoupbud.view.adapters.ShoppingCartAdapter;
import com.project.ishoupbud.view.dialog.ConfirmationTransactionDialogFragment;
import com.project.ishoupbud.view.holders.ShoppingCartHolder;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.listeners.ClickEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/12/17.
 */

public class ShoppingCartActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_wishlist) RecyclerView rvShoppingCart;
    @BindView(R.id.tv_total_price) TextView tvTotalPrice;
    @BindView(R.id.btn_continue) Button btnContinue;

    ShoppingCartAdapter<ShoppingCart> shoppingCartAdapter;

    ConfirmationTransactionDialogFragment confirmationTransactionDialogFragment;

    int selectedIdx;
    float totalPrice = 0;
    User user;
    Vendor vendor;

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
        shoppingCartAdapter.onValueChangeListener = new StepperView.OnValueChangeListener() {
            @Override
            public void onValueChangeByButtonClick(int value) {
                validateList();
            }
        };
        shoppingCartAdapter.setItemListener(R.id.ib_delete, new ClickEventListener<ShoppingCart>() {
            @Override
            public void onClick(View v, ShoppingCart shoppingCart, int position) {
                deleteItem(shoppingCart.id);
                selectedIdx = position;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        rvShoppingCart.addItemDecoration(new InsetDividerItemDecoration(this));
        rvShoppingCart.setLayoutManager(layoutManager);
        rvShoppingCart.setAdapter(shoppingCartAdapter);

        tvTotalPrice.setText("Total: Rp. 0");

        initProgressDialog("Deleting Items...");

        user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER),User.class);

        getCart();
    }

    public void getCart(){
        Call<GenericResponse<ShoppingCartResponse>> getCartRequest = APIManager.getRepository(ShoppingCartRepo.class).getCart();
        getCartRequest.enqueue(new APICallback<GenericResponse<ShoppingCartResponse>>() {
            @Override
            public void onSuccess(Call<GenericResponse<ShoppingCartResponse>> call, Response<GenericResponse<ShoppingCartResponse>> response) {
                super.onSuccess(call, response);
                shoppingCartAdapter.setNew(response.body().data.shoppingCarts);
                if(shoppingCartAdapter.getItemCount() > 0){
                    vendor = shoppingCartAdapter.getItemAt(0).product.vendor;
                }
                tvTotalPrice.setText("Total: " + Utils.indonesiaFormat(response.body().data.total));
            }

            @Override
            public void onFailure(Call<GenericResponse<ShoppingCartResponse>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void deleteItem(int cart_id){
        progressDialog.setMessage("Deleting Items...");
        progressDialog.show();
        Call<com.project.michael.base.models.Response> deleteCart = APIManager.getRepository(ShoppingCartRepo.class).deleteCart(cart_id);
        deleteCart.enqueue(new APICallback<com.project.michael.base.models.Response>() {
            @Override
            public void onNoContent(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                super.onNoContent(call, response);
                shoppingCartAdapter.remove(selectedIdx);
                progressDialog.dismiss();
                validateList();
            }

            @Override
            public void onFailure(Call<com.project.michael.base.models.Response> call, Throwable t) {
                super.onFailure(call, t);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                super.onError(call, response);
                progressDialog.dismiss();
            }
        });
    }

    public void doCheckOut(){
        progressDialog.setMessage("Check out...");
        progressDialog.show();
        APIManager.getRepository(TransactionRepo.class).checkout()
            .enqueue(new APICallback<GenericResponse<List<Transaction>>>() {
                @Override
                public void onSuccess(Call<GenericResponse<List<Transaction>>> call, Response<GenericResponse<List<Transaction>>> response) {
                    super.onSuccess(call, response);
                    confirmationTransactionDialogFragment.dismiss();
                    progressDialog.cancel();
                    progressDialog.dismiss();
                    confirmationTransactionDialogFragment.dismiss();
                }

                @Override
                public void onFailure(Call<GenericResponse<List<Transaction>>> call, Throwable t) {
                    super.onFailure(call, t);
                    progressDialog.dismiss();
                }

                @Override
                public void onError(Call<GenericResponse<List<Transaction>>> call, Response<GenericResponse<List<Transaction>>> response) {
                    super.onError(call, response);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Saldo tidak cukup", Toast.LENGTH_SHORT).show();
                }
            });

    }

    public void validateList(){
        totalPrice = 0;
        StepperView stepperView;
        for(int i = 0; i< shoppingCartAdapter.getItemCount(); i++){
            stepperView = ((ShoppingCartHolder)rvShoppingCart.findViewHolderForAdapterPosition(i)).stepperView;
            totalPrice += stepperView.getValue() * shoppingCartAdapter.getItemAt(i).product.price;
        }
        tvTotalPrice.setText("Total: " + Utils.indonesiaFormat(totalPrice));
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
                confirmationTransactionDialogFragment = new ConfirmationTransactionDialogFragment();
                confirmationTransactionDialogFragment.show(getSupportFragmentManager(),"Confirmation Transaction",user, vendor);
                break;
        }
    }
}
