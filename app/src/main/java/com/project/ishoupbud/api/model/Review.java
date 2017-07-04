package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by michael on 4/10/17.
 */

public class Review {

    public int id;
    public User user;
    public Vendor vendor;
    @SerializedName("body")
    public String description;
    public double rating;
    public Date date;
    @SerializedName("sentimen")
    public Sentiment sentiment;
    public boolean status;
    public Product product;
}
