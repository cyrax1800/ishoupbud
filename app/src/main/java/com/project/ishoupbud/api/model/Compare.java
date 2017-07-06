package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by michael on 7/6/17.
 */

public class Compare {

    @SerializedName("target")
    public List<Product> products;
    public Product source;
}
