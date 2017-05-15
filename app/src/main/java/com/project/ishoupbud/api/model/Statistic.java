package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by michael on 5/15/17.
 */

public class Statistic {
    public int id;
    public float value;
    public Date date;
    @SerializedName("vendor")
    public int vendorId;
    @SerializedName("product")
    public int productId;
}
