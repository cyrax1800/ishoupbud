package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 4/9/17.
 */

public class Product extends GenericResponse<Product>{

    public int id;
    public String barcode;
    public String name;
    public int price;
    public double ratingSummary;
    public String description;
    @SerializedName("picture_url")
    public PictureSize pictureUrl;

    public Product(){

    }

    public Product(int id, String name, int price, double ratingSummary, String picUrl){
        this.id = id;
        this.name = name;
        this.price = price;
        this.ratingSummary = ratingSummary;
        this.pictureUrl = new PictureSize();
        this.pictureUrl.medium = "";
    }

    public static Product createDummy(){
        return new Product(1,"asdsad",Utils.random(1,100) * 1000, Utils.randomFloat(0,5,1),"asdsad");
    }

    public static List<Product> getDummy(int count){
        List<Product> list = new ArrayList<>();
        for(int i = 0; i< count;i++){
            list.add(createDummy());
        }
        return list;
    }
}
