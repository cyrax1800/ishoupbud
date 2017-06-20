package com.project.ishoupbud.api.Uber.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by michael on 6/20/17.
 */

public class UberPrice {

    @SerializedName("localized_display_name")
    public String LocalizeDisplayName;
    public Double distance;
    @SerializedName("display_name")
    public String displayName;
    @SerializedName("high_estimate")
    public int highEstimate;
    @SerializedName("low_estimate")
    public int lowEstimate;
}
