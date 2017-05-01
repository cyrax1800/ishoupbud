package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by michael on 4/30/17.
 */

@RealmClass
public class WishList implements RealmModel {

    @PrimaryKey
    public int id;
    public Product product;
    @SerializedName("product_id")
    public int productId;

    public WishList(){

    }
}
