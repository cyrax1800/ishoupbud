package com.project.ishoupbud.api.model;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.annotations.SerializedName;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by michael on 4/9/17.
 */
public class Product extends RealmObject implements SearchSuggestion {

    public int id;
    public String barcode;
    public String name;
    @SerializedName(value="minimum_price", alternate={"price"})
    public int price;
    @SerializedName("avg_rating")
    public double totalRating;
    @SerializedName("total_review")
    public int totalReview;
    public String description;
    @SerializedName("picture_url")
    public PictureSize pictureUrl;
    public Vendor vendor;
    public RealmList<ProductVendors> vendors;

    public Product(){

    }

    public Product(int id, String name, int price, double ratingSummary, String picUrl){
        this.id = id;
        this.name = name;
        this.price = price;
        this.totalRating = ratingSummary;
        this.pictureUrl = new PictureSize();
        this.pictureUrl.medium = "";
    }

    public Product(Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
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

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


    @Override
    public String getBody() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
