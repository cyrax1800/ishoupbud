package com.project.ishoupbud.api.model;

import java.util.List;

/**
 * Created by michael on 6/27/17.
 */

public class ShoppingCartContainer implements Cloneable{

    public int id;
    public Vendor vendor;
    public List<ShoppingCart> item;
    public float subTotal;
    public boolean isFetching = false;
    public String distance;
    public String duration;
    public int shippingPrice;

    public ShoppingCartContainer(){

    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
