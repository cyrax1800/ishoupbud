package com.project.ishoupbud.api.response;

import com.google.gson.annotations.SerializedName;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.michael.base.models.Response;

import java.util.List;

/**
 * Created by michael on 5/15/17.
 */

public class ShoppingCartResponse {

    @SerializedName("items")
    public List<ShoppingCart> shoppingCarts;
    public float total;
}
