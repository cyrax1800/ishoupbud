package com.project.ishoupbud.view.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.view.activities.ShoppingCartActivity;
import com.project.ishoupbud.view.holders.ShoppingCartContainerHolder;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.FastAdapter;
import com.project.michael.base.views.helpers.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 6/27/17.
 */

public class ShoppingCartContainerAdapter<Model> extends FastAdapter<Model> {

    public ShoppingCartActivity shoppingCartActivity;
    public List<Boolean> checkedIdx;

    public ShoppingCartContainerAdapter(ShoppingCartActivity shoppingCartActivity) {
        this.shoppingCartActivity = shoppingCartActivity;
        checkedIdx = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_shopping_cart_container, parent, false);
        final ShoppingCartContainerHolder shoppingCartHolder =
                new ShoppingCartContainerHolder(itemView, this, viewType);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(shoppingCartActivity.getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false);

        shoppingCartHolder.rvCart
                .addItemDecoration(
                        new DividerItemDecoration(shoppingCartActivity.getApplicationContext(),
                                LinearLayoutManager.VERTICAL)
                );
        shoppingCartHolder.rvCart.setLayoutManager(layoutManager);


        shoppingCartHolder.chkVendor
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            shoppingCartHolder.chkVendor.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            shoppingCartHolder.chkVendor.setTypeface(Typeface.DEFAULT);
                        }
                    }
                });

        return super.onPostCreateViewHolder(shoppingCartHolder, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ShoppingCartContainerHolder shoppingCartHolder =
                (ShoppingCartContainerHolder) holder;
        ShoppingCartContainer shoppingCartContainer =
                (ShoppingCartContainer) mModelList.get(position);
        shoppingCartHolder.shoppingCartAdapter.setNew(shoppingCartContainer.item);
        shoppingCartHolder.setBind(this, position);
        shoppingCartHolder.rvCart.setAdapter(shoppingCartHolder.shoppingCartAdapter);
        shoppingCartHolder.chkVendor.setText(shoppingCartContainer.vendor.name);
        shoppingCartHolder.rvCart.post(new Runnable() {
            @Override
            public void run() {
                validateList(shoppingCartHolder);
            }
        });
        if (position > checkedIdx.size() - 1) {
            while (position > checkedIdx.size() - 1) {
                checkedIdx.add(false);
            }
        }
    }

    public void delete(int id, final int pos) {
        APIManager.getRepository(ShoppingCartRepo.class).deleteCart(id)
                .enqueue(new APICallback<com.project.michael.base.models.Response>() {
                    @Override
                    public void onError(Call<com.project.michael.base.models.Response> call,
                                        Response<com.project.michael.base.models.Response>
                                                response) {
                        super.onError(call, response);
                        shoppingCartActivity.dismissDialog();
                    }

                    @Override
                    public void onNoContent(Call<com.project.michael.base.models.Response> call,
                                            Response<com.project.michael.base.models.Response>
                                                    response) {
                        super.onNoContent(call, response);
                        remove(pos);
                        notifyDataSetChanged();
                        shoppingCartActivity.dismissDialog();
                    }

                    @Override
                    public void onFailure(Call<com.project.michael.base.models.Response> call,
                                          Throwable t) {
                        super.onFailure(call, t);
                        shoppingCartActivity.dismissDialog();
                    }
                });
    }

    public void validateList(ShoppingCartContainerHolder shoppingCartContainerHolder) {
        float total = 0;
        for (int i = 0; i < shoppingCartContainerHolder.shoppingCartAdapter.getItemCount(); i++) {
            if (shoppingCartContainerHolder.rvCart.findViewHolderForAdapterPosition(i) == null)
                continue;
            total += shoppingCartContainerHolder.shoppingCartAdapter.getItemAt(i).quantity *
                    shoppingCartContainerHolder.shoppingCartAdapter.getItemAt(i).product.price;
        }
        int position = shoppingCartContainerHolder.getAdapterPosition();
        ShoppingCartContainer shoppingCartContainer =
                (ShoppingCartContainer) mModelList.get(position);
        shoppingCartContainer.subTotal = total;
        shoppingCartContainerHolder.tvSubTotal.setText(Utils.indonesiaFormat(total));
        shoppingCartActivity.calculateTotal();

    }

    public void setCheck(ShoppingCartContainerHolder shoppingCartContainerHolder,
                         int idx,
                         Boolean isChecked) {
        checkedIdx.set(idx, isChecked);
        shoppingCartContainerHolder.chkVendor.setChecked(isChecked);
        if (isChecked) shoppingCartContainerHolder.btmContainer.setVisibility(View.GONE);
        else shoppingCartContainerHolder.btmContainer.setVisibility(View.VISIBLE);
    }

    public void toggleChecked(ShoppingCartContainerHolder shoppingCartContainerHolder, int idx) {
        boolean isChecked = shoppingCartContainerHolder.chkVendor.isChecked();
        checkedIdx.set(idx, !isChecked);
        shoppingCartContainerHolder.chkVendor.setChecked(!isChecked);
        shoppingCartActivity.validateChecklist();
    }
}
