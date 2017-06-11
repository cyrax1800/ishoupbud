package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;
import com.project.michael.base.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 4/13/17.
 */

public class ShoppingCart {
    public int id;
    public int quantity;
    public double harga;
    @SerializedName(value="items", alternate={"item"})
    public Product product;
    public ProductVendors vendor;

    public ShoppingCart(){

    }

    public ShoppingCart(int id, int quantity, Product product, ProductVendors vendor){
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.vendor = vendor;
    }

    public static ShoppingCart createDummy(){
        return new ShoppingCart(1,Utils.random(1,100), Product.createDummy(), ProductVendors.createDummy());
    }

    public static List<ShoppingCart> getDummy(int count){
        List<ShoppingCart> list = new ArrayList<>();
        for(int i = 0; i< count;i++){
            list.add(createDummy());
        }
        return list;
    }
}
