package com.project.ishoupbud.api.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by michael on 4/30/17.
 */

public class ProductVendors extends RealmObject {

    public int id;
    public double price;
    public Vendor vendor;
    public String distance;
    public int shippingPrice;


    public ProductVendors(){

    }

    public ProductVendors(int id, Vendor vendor){
        this.id = id;
        this.vendor = vendor;
    }

    public static ProductVendors createDummy(){
        return new ProductVendors(1, Vendor.createDummy());
    }

    public static List<ProductVendors> getDummy(int count){
        List<ProductVendors> list = new ArrayList<>();
        for(int i = 0; i< count;i++){
            list.add(createDummy());
        }
        return list;
    }

}
