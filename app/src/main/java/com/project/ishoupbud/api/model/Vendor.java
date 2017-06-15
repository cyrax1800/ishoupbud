package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;
import com.project.michael.base.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by michael on 4/10/17.
 */

public class Vendor extends RealmObject {

    public int id;
    public String name;
    public String address;
    @SerializedName("lng")
    public Double longitude;
    @SerializedName("lat")
    public Double latitude;
    public String picture_url;

    public Vendor(){

    }

    public Vendor(int id, String name, String address, int price){
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public static Vendor createDummy(){
        return new Vendor(1, "HyperMart", "asdsad",Utils.random(1,100) * 1000);
    }

    public static List<Vendor> getDummy(int count){
        List<Vendor> list = new ArrayList<>();
        for(int i = 0; i< count;i++){
            list.add(createDummy());
        }
        return list;
    }
}
