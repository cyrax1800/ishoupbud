package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.view.StepperView;
import com.project.ishoupbud.view.holders.ShoppingCartContainerHolder;
import com.project.ishoupbud.view.holders.ShoppingCartHolder;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.FastAdapter;
import com.project.michael.base.views.listeners.ClickEventListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/13/17.
 */

public class ShoppingCartAdapter<Model> extends FastAdapter<Model> {

    public StepperView.OnValueChangeListener onValueChangeListener;
    private ShoppingCartContainerHolder containerHolder;
    private ShoppingCartContainerAdapter containerAdapter;
    private int containerPosition;
    private int containerId;
    private Call<GenericResponse<ShoppingCartContainer>> updateCaller;

    public ShoppingCartAdapter() {

    }

    public ShoppingCartAdapter(ShoppingCartContainerHolder holder, ShoppingCartContainerAdapter
            adapter,
                               int position) {
        this.containerHolder = holder;
        this.containerAdapter = adapter;
        this.containerPosition = position;
        this.containerId = ((ShoppingCartContainer) adapter.getItemAt(position)).id;
        onValueChangeListener =
                new StepperView.OnValueChangeListener() {

                    @Override
                    public void onValueChangeByButtonClick(int value, int position) {
                        if (position == -1) return;
                        updateItem(value, position);

                    }
                };

        setItemListener(R.id.ib_delete, new ClickEventListener<ShoppingCart>() {
            @Override
            public void onClick(View v, ShoppingCart shoppingCart, int position) {
                deleteItem(position);
//                selectedIdx = position;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_shopping_cart, parent, false);
        return super.onPostCreateViewHolder(new ShoppingCartHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShoppingCartHolder shoppingCartHolder = (ShoppingCartHolder) holder;
        ShoppingCart shoppingCart = (ShoppingCart) mModelList.get(position);

        shoppingCart.harga = shoppingCart.harga/shoppingCart.quantity;
        shoppingCartHolder.tvProductName.setText(shoppingCart.product.name);
        shoppingCartHolder.tvPrice.setText(Utils.indonesiaFormat(shoppingCart.harga));
        shoppingCartHolder.stepperView.setValue(shoppingCart.quantity);
        shoppingCartHolder.stepperView.setOnValueChangeListener(onValueChangeListener, position);

        Glide
                .with(shoppingCartHolder.ivProduct.getContext())
                .load(shoppingCart.product.pictureUrl.small)
                .centerCrop()
                .crossFade()
                .into(shoppingCartHolder.ivProduct);

    }

    public void updateItem(final int value, final int position) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("quantity", value);
        if((updateCaller != null) && updateCaller.isExecuted()){
            updateCaller.cancel();
        }
        if(position >= containerHolder.shoppingCartAdapter.getItemCount()) return;
        updateCaller = APIManager.getRepository(ShoppingCartRepo.class).updateCart(map, containerId,
                containerHolder.shoppingCartAdapter.getItemAt(position).id);
        updateCaller.enqueue(new APICallback<GenericResponse<ShoppingCartContainer>>() {
                    @Override
                    public void onSuccess(Call<GenericResponse<ShoppingCartContainer>> call,
                                          Response<GenericResponse<ShoppingCartContainer>>
                                                  response) {
                        super.onSuccess(call, response);
                        ShoppingCart shoppingCart = containerHolder.shoppingCartAdapter.getItemAt
                                (position);
                        shoppingCart.quantity = response.body().data.item.get(position).quantity;

                        mModelList.set(position, (Model) shoppingCart);
                        containerAdapter.validateList(containerHolder);
                    }

                    @Override
                    public void onFailure(Call<GenericResponse<ShoppingCartContainer>> call,
                                          Throwable t) {
                        super.onFailure(call, t);
                    }

                    @Override
                    public void onError(Call<GenericResponse<ShoppingCartContainer>> call,
                                        Response<GenericResponse<ShoppingCartContainer>> response) {
                        super.onError(call, response);
                    }
                });
    }

    public void deleteItem(final int position) {
        containerAdapter.shoppingCartActivity.showDialog("Deleting Item...");
        if((updateCaller != null) && updateCaller.isExecuted()){
            updateCaller.cancel();
        }
        APIManager.getRepository(ShoppingCartRepo.class).deleteItemCart(containerId,
                containerHolder.shoppingCartAdapter.getItemAt(position).id)
                .enqueue(new APICallback<com.project.michael.base.models.Response>() {

                    @Override
                    public void onNoContent(Call<com.project.michael.base.models.Response> call,
                                            Response<com.project.michael.base.models.Response>
                                                    response) {
                        super.onNoContent(call, response);
                        remove(position);
                        if (mModelList.size() == 0) {
                            containerAdapter.delete(containerId, containerPosition);
                        } else {
                            containerAdapter.validateList(containerHolder);
                            containerAdapter.shoppingCartActivity.dismissDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.project.michael.base.models.Response> call,
                                          Throwable t) {
                        super.onFailure(call, t);
                    }

                    @Override
                    public void onError(Call<com.project.michael.base.models.Response> call,
                                        Response<com.project.michael.base.models.Response>
                                                response) {
                        super.onError(call, response);
                    }
                });
    }

    public void bindTo(ShoppingCartContainerHolder holder, ShoppingCartContainerAdapter adapter,
                       int position) {
        this.containerHolder = holder;
        this.containerAdapter = adapter;
        this.containerPosition = position;
        this.containerId = ((ShoppingCartContainer) adapter.getItemAt(position)).id;
    }
}
